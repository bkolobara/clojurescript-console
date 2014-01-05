(defproject cljsconsole "0.1.0"
  :description "An extension to the Chrome developer tools allowing the evaluation of ClojureScript
                code directly in the browser."
  :url "https://github.com/bkolobara/clojurescript-console"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2134"]
                 [org.clojure/core.async "0.1.256.0-1bf8cf-alpha"] 
                 [om "0.1.4"]]
  :plugins [[lein-cljsbuild "1.0.1"]]
  :cljsbuild {
    :builds [{:source-paths ["src/cljsconsole"]
              :compiler {
                :output-to "dist/chrome/cljsconsole.js"
                :optimizations :whitespace}}
             {:source-paths ["src/panel"]
              :compiler {
                :output-to "dist/chrome/panel.js"
                :optimizations :whitespace
                :preamble ["react/react.min.js"]
                :externs ["react/externs/react.js"]}}]})

