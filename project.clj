(defproject cljsconsole "0.1.0"
  :description "An extension to the Chrome developer tools allowing the evaluation of ClojureScript
                code directly in the browser."
  :url "https://github.com/bkolobara/cljsconsole"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :plugins [[lein-cljsbuild "0.3.3"]]
  :cljsbuild {
    :builds [{
      :source-paths ["src"]
      :compiler {
        :output-to "dist/chrome/cljsconsole.js"
        :optimizations :whitespace
        :pretty-print true}}]})

