(ns main)

(js/chrome.devtools.panels.create "ClojureScript"
                                  "clojure-logo.png"
                                  "panel.html")

