(ns main
  (:require [clojure.string :as string]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(def app-state
  (atom {}))

(defn past-event [{:keys [text]} owner opts]
  (om/component
    (dom/div #js {:className "past-event"}
             text)))

(defn history [{:keys [history]}]
  (om/component
    (dom/div #js {:className "history"}
             (om/build-all past-event history))))

(def ENTER_KEY 13)

(defn handle-expression-keydown [e app owner]
  (when (== (.-which e) ENTER_KEY)
    (let [expression-node (om/get-node owner "expression")
          expression (.. expression-node -value trim)]
      (when-not (string/blank? expression)
        (om/transact! app :history conj {:text expression})
        (set! (.-value expression-node) "")))
    false))

(defn now [app owner]
  (om/component
    (dom/div #js {:className "now"}
             (dom/div #js {:className "left"} ">")
             (dom/div #js {:className "middle"}
             (dom/input #js {:type "text"
                             :ref "expression"
                             :onKeyDown #(handle-expression-keydown % app owner)}))
             (dom/div #js {:className "right"} ""))))

(defn console [app owner opts]
  (reify
    om/IInitState
    (init-state [_]
      (om/transact! app [:history] (fn [] [])))
    om/IRender
    (render [_]
      (dom/div #js {:className "console"}
        (om/build history app)
        (om/build now app)))))

(om/root app-state console (.getElementById js/document "console"))

