(defproject minesweeper "0.9"
  :description "Minesweeper"
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.2"]
                 [ring "1.2.2"]
                 [com.taoensso/carmine "2.9.0"]
                 [org.clojure/data.json "0.2.6"]]
  :plugins [[lein-ring "0.8.13"]])

