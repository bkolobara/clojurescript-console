(ns cljsconsole.core
  (:require [enfocus.core :as ef]))

(js/chrome.devtools.panels.create "ClojureScript"
                                  "clojure-logo.png"
                                  "panel.html")


