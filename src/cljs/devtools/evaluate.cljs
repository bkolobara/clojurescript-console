(ns evaluate
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.reader :as reader]
            [cljs.core.async :refer [put! <! chan close!]]
            [messages :refer [regular-message warning-message error-message]]))

; Himera compilation server URL
(def SERVER "http://himera.herokuapp.com/compile")

; Channel for adding messages to command history
(def history-messages (chan))

; Holding the chrome communication chanel to the POST request in backend.js
(def backend-POST (js/chrome.runtime.connect #js {:name "backend-POST"}))

(defn himera-compile
  "Sends an expression for compilation to a himera service running
  on the url and returns a chanel that will have the JavaScript."
  [url expression]
  (let [ch (chan)]
    (.postMessage backend-POST #js {:url url :data expression})
    ; When an answer is received put it in the channel
    (.addListener backend-POST.onMessage
      (fn [answer] (put! ch answer)))
    ch))

(defn evaluate-expression-in-inspected-window
  "Returns a channel with the result from the eval function or information
  about the exception that occurred."
  [expression]
  (let [ch (chan)]
    (js/chrome.devtools.inspectedWindow.eval expression
      (fn [result exception]
        (if exception
          (put! ch (error-message (.-value exception)))
          (put! ch (regular-message result)))))
    ch))

(defn format-expression-for-himera
  "Returns the data that the himera server understands."
  [expression]
  (str "{:expr " expression "}"))

(defn get-himera-response-javascript
  "Returns himera response javastring as string."
  [response]
  (:js (reader/read-string response)))

(defn compile-evaluate-expression [expression]
  (go (let [response (<! (himera-compile SERVER
                                         (format-expression-for-himera expression)))]
        (if (not (= (.-status response) 200))
          (do ;; Compiler error happend on server. Himera doesn't give us an erro message :(
            (put! history-messages (error-message "Compilation error :(")))

          (do ;; Expression successfully compiled
            (go (let [result (<! (evaluate-expression-in-inspected-window
                                   (get-himera-response-javascript (.-text response))))]
                 (put! history-messages result))))))))
