(defproject cljsconsole "0.0.2"
  :description "An extension to the Chrome developer tools allowing the evaluation of ClojureScript
                code directly in the browser."
  :url "https://github.com/bkolobara/clojurescript-console"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.1.5"]
                 [cljs-http "0.1.2"]]
  :plugins [[lein-cljsbuild "1.0.1"]]
  :cljsbuild {
    :builds [{:source-paths ["src/cljs/devtools"]
              :compiler {
                :output-to "dist/chrome/devtools.js"
                :optimizations :simple
                :preamble ["react/react.min.js"]
                :externs ["react/externs/react.js"]}}
             {:source-paths ["src/cljs/background"]
              :compiler {
                :output-to "dist/chrome/background.js"
                :optimizations :simple}}]})

