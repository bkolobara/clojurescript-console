(ns core
  (:require [goog.net.XhrIo :as xhr]))

; Because the devtool extension panel is loaded into an iframe the permissions
; granted to the extension don't apply for it. So we use this background script
; to do Cross-Origin XMLHttpRequest. This script communicates with the devtool
; script through Chrom's API for message passing.

(defn doPOST
  "POST request to url."
  [url data port-back]

    (xhr/send url
              (fn [event]
                (let [text (-> event .-target .getResponseText)
                      status (-> event .-target .getStatus)]
                  (.postMessage port-back #js {:text text :status status})))
              "POST"
              data
              #js {:Content-Type "application/clojure; charset=utf-8"}))

; Listen for incoming messages
(.addListener js/chrome.runtime.onConnect
  (fn [port-back]
    (if (= (.-name port-back) "backend-POST")
      (.addListener port-back/onMessage
        (fn [msg]
          (doPOST msg/url msg/data port-back))))))
