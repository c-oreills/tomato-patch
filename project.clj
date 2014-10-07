(defproject tomato-patch "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2268"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.6.5"]
                 [garden "1.1.3"]
                 [com.andrewmcveigh/cljs-time "0.1.6"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]
            [lein-garden "0.1.1"]]

  :source-paths ["src/clj"]

  :cljsbuild {
    :builds [{:id "tomato-patch"
              :source-paths ["src/cljs"]
              :compiler {
                         :output-to "out/js/tomato-patch.js"
                         :output-dir "out"
                         :optimizations :none
                         :source-map true}}]}

  :garden {:builds [{:stylesheet tomato-patch.style/style
                     :compiler {:output-to "out/css/tomato_patch.css"
                                :vendors ["webkit" "moz" "o"]
                                :pretty-print? true}}]}

  :main ^:skip-aot tomato-patch.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
