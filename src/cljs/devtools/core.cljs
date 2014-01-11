(ns core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<!]]
            [evaluate :refer [history-messages compile-evaluate-expression]]))

(def ENTER_KEY 13)
(def UP_KEY 38)
(def DOWN_KEY 40)


(def app-state
  (atom {}))

(def command-history
  (atom (list)))

(def COMMAND_HISTORY_STARTING_POSITION -1)
(def command-history-position
  (atom COMMAND_HISTORY_STARTING_POSITION))


(defn error-message
  "Om component representing an error message"
  [text]
  (om/component
    (dom/div #js {:className "message"}
      (dom/div #js {:className "error-left"}
        (dom/span #js {:className "icon-cancel-circle"}))
      (dom/div #js {:className "error-message"} text)
      (dom/div #js {:className "error-right"} ""))))

(defn warning-message
  "Om component representing a warning message"
  [text]
  (om/component
    (dom/div #js {:className "message"}
      (dom/div #js {:className "warning-left"}
        (dom/span #js {:className "icon-notification"}))
      (dom/div #js {:className "error-message"} text)
      (dom/div #js {:className "warning-right"} ""))))

(defn regular-message
  "Om component representing a regular message"
  [text]
  (om/component
    (dom/div #js {:className "message"}
      (dom/div #js {:className "regular-left"}
        (dom/span #js {:className "icon-info"}))
      (dom/div #js {:className "regular-message"} text)
      (dom/div #js {:className "regular-right"} ""))))

(defn old-expression-message
  "Om component representing a old command message."
  [text]
  (om/component
    (dom/div #js {:className "message"}
      (dom/div #js {:className "old-expression-left"}
        (dom/span #js {:className "icon-arrow-right"}))
      (dom/div #js {:className "old-expression-message"} text)
      (dom/div #js {:className "old-expression-right"} ""))))

(defn history-message
  "Om component representing a message stored in history."
  [{:keys [message-type message-text]}]
  (cond
    (= message-type :error) (error-message message-text)
    (= message-type :warning) (warning-message message-text)
    (= message-type :old-expression) (old-expression-message message-text)
    :else (regular-message message-text)))


(defn history
  "Om component representing history of past expressions and results."
  [{:keys [history]}]
  (om/component
    (dom/div #js {:className "history"}
             (om/build-all history-message history))))


(defn handle-prompt-keydown
  "Function that handles submit on enter and history travel on up and down keys."
  [event owner app]
  (let [prompt-node (om/get-node owner "expression")
        expression (.. prompt-node -value trim)]
    (condp == (.-which event)
      ENTER_KEY
        (when-not (string/blank? expression)
          (compile-evaluate-expression expression)
          (swap! command-history conj expression)
          (reset! command-history-position COMMAND_HISTORY_STARTING_POSITION)
          (set! (.-value prompt-node) "")
          false)
      UP_KEY
        (when (< @command-history-position
                 (+ (count @command-history) COMMAND_HISTORY_STARTING_POSITION))
          (swap! command-history-position inc)
          (set! (.-value prompt-node) (nth  @command-history
                                             @command-history-position))
          false)
      DOWN_KEY
        (when (> @command-history-position COMMAND_HISTORY_STARTING_POSITION)
          (swap! command-history-position dec)
          (if (= @command-history-position COMMAND_HISTORY_STARTING_POSITION)
            (set! (.-value prompt-node) "")
            (set! (.-value prompt-node) (nth  @command-history
                                               @command-history-position)))
          false)
      ; Pass event to default handler
      event)))

(defn prompt
  "Om component representing the current expression that is typed in."
  [app owner]
  (dom/div #js {:className "prompt"}
           (dom/div #js {:className "prompt-left"}
             (dom/span #js {:className "icon-arrow-right"}))
           (dom/div #js {:className "prompt-middle"}
             (dom/input #js {:type "text"
                             :autoFocus "autofocus"
                             :ref "expression"
                             :onKeyDown #(handle-prompt-keydown % owner app)}))
           (dom/div #js {:className "prompt-right"} "")))

(defn handle-refocus-prompt
  "Focus the prompt
  TODO: Maybe don't return false to allow click propagation if later needed.
  TODO: Figure out how to focus withou scroll"
  [event owner]
  (let [prompt-node (om/get-node owner "expression")]
    (.focus prompt-node))
  false)

(defn console
  "Main component, covering the whole panel and containing history
  and prompt components."
  [app owner opts]
  (reify
    om/IInitState
    (init-state [_]
      ;; Initialize app state
      (om/transact! app :history (fn [] (vector))))

    om/IWillMount
    (will-mount [_]
      (go (while true
            (let [new-history-message (<! history-messages)]
              (om/transact! app :history conj new-history-message)))))

    om/IRender
    (render [_]
      (dom/div #js {:className "console-element"
                    :onClick #(handle-refocus-prompt % owner)}
        (om/build history app)
        (prompt app owner)))))

(defn initialize
  "Bind om's root element to devtool panel's console div."
  [panel]
  (.addListener (.-onShown panel)
    (fn [panel-window]
      (om/root app-state
               console
               (.getElementById (.-document panel-window)
                                "console")))))


; Create devtools panel and pass the panel reference to the initialize function.
(.create chrome.devtools.panels "ClojureScript"
                                "clojure-logo.png"
                                "panel.html"
                                initialize)
