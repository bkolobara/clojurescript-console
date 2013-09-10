(ns cljsconsole.core)

(js/chrome.devtools.panels.create "ClojureScript"
                                  "clojure-logo.png"
                                  "panel.html")

