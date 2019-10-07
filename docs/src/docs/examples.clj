(ns docs.examples)

;; tag::include-crux-api[]
(require '[crux.api :as crux])
(import (crux.api ICruxAPI))
;; end::include-crux-api[]

;; tag::require-ek[]
(require '[crux.kafka.embedded :as ek])
;; end::require-ek[]

(defn example-start-standalone []
;; tag::start-standalone-node[]
(def ^crux.api.ICruxAPI node
  (crux/start-standalone-node {:kv-backend "crux.kv.memdb.MemKv"
                               :db-dir "data/db-dir-1"
                               :event-log-dir "data/eventlog-1"}))
;; end::start-standalone-node[]
node)

(defn example-close-node [node]
;; tag::close-node[]
(.close node)
;; end::close-node[]
 )

(defn example-start-embedded-kafka []
;; tag::ek-example[]
(def storage-dir "dev-storage")
(def embedded-kafka-options
  {:crux.kafka.embedded/zookeeper-data-dir (str storage-dir "/zookeeper")
   :crux.kafka.embedded/kafka-log-dir (str storage-dir "/kafka-log")
   :crux.kafka.embedded/kafka-port 9092})

(def embedded-kafka (ek/start-embedded-kafka embedded-kafka-options))
;; end::ek-example[]
embedded-kafka)

(defn example-stop-embedded-kafka [embedded-kafka]
;; tag::ek-close[]
(.close embedded-kafka)
;; end::ek-close[]
)

(defn example-start-cluster []
;; tag::start-cluster-node[]
(def ^crux.api.ICruxAPI node
  (crux/start-cluster-node {:kv-backend "crux.kv.memdb.MemKv"
                            :bootstrap-servers "localhost:9092"}))
;; end::start-cluster-node[]
node)

(defn example-start-rocks []
;; tag::start-standalone-with-rocks[]
(def ^crux.api.ICruxAPI node
  (crux/start-standalone-node {:kv-backend "crux.kv.rocksdb.RocksKv"
                               :db-dir "data/db-dir-1"
                               :event-log-dir "data/eventlog-1"}))
;; end::start-standalone-with-rocks[]
node)

(defn example-start-jdbc []
;; tag::start-jdbc-node[]
(def ^crux.api.ICruxAPI node
  (crux/start-jdbc-node {:dbtype "postgresql"
                         :dbname "cruxdb"
                         :host "<host>"
                         :user "<user>"
                         :password "<password>"})
;; end::start-jdbc-node[]
  ))

(defn example-submit-tx [node]
;; tag::submit-tx[]
(crux/submit-tx
 node
 [[:crux.tx/put
   {:crux.db/id :dbpedia.resource/Pablo-Picasso ; id
    :name "Pablo"
    :last-name "Picasso"}
   #inst "2018-05-18T09:20:27.966-00:00"]]) ; valid time
;; end::submit-tx[]
)

(defn example-query [node]
;; tag::query[]
(crux/q (crux/db node)
        '{:find [e]
          :where [[e :name "Pablo"]]})
;; end::query[]
)

(defn example-query-entity [node]
;; tag::query-entity[]
(crux/entity (crux/db node) :dbpedia.resource/Pablo-Picasso)
;; end::query-entity[]
)

(defn example-query-valid-time [node]
;; tag::query-valid-time[]
(crux/q (crux/db node #inst "2018-05-19T09:20:27.966-00:00")
        '{:find [e]
          :where [[e :name "Pablo"]]})
;; end::query-valid-time[]
  )

(defn query-example-setup [node]
  (let [maps
        ;; tag::query-input[]
        [{:crux.db/id :ivan
          :name "Ivan"
          :last-name "Ivanov"}

         {:crux.db/id :petr
          :name "Petr"
          :last-name "Petrov"}

         {:crux.db/id :smith
          :name "Smith"
          :last-name "Smith"}]
        ;; end::query-input[]
        ]

    (crux/submit-tx node
                   (vec (for [m maps]
                          [:crux.tx/put m])))))

(defn query-example-basic-query [node]
 (crux/q
  (crux/db node)
 ;; tag::basic-query[]
 '{:find [p1]
   :where [[p1 :name n]
           [p1 :last-name n]
           [p1 :name "Smith"]]}
 ;; end::basic-query[]
 ))

(defn query-example-with-arguments-1 [node]
 (crux/q
  (crux/db node)
  ;; tag::query-with-arguments1[]
 {:find '[n]
  :where '[[e :name n]]
  :args [{'e :ivan
          'n "Ivan"}]}
  ;; end::query-with-arguments1[]
  ))

(defn query-example-with-arguments-2 [node]
 (crux/q
  (crux/db node)
  ;; tag::query-with-arguments2[]
 {:find '[e]
  :where '[[e :name n]]
  :args [{'n "Ivan"}
         {'n "Petr"}]}
  ;; end::query-with-arguments2[]
  ))

(defn query-example-with-arguments-3 [node]
 (crux/q
  (crux/db node)
  ;; tag::query-with-arguments3[]
 {:find '[e]
  :where '[[e :name n]
           [e :last-name l]]
  :args [{'n "Ivan" 'l "Ivanov"}
         {'n "Petr" 'l "Petrov"
          }]}
  ;; end::query-with-arguments3[]
  ))

(defn query-example-with-arguments-4 [node]
 (crux/q
  (crux/db node)
 ;; tag::query-with-arguments4[]
 {:find '[n]
  :where '[[(re-find #"I" n)]
           [(= l "Ivanov")]]
  :args [{'n "Ivan" 'l "Ivanov"}
         {'n "Petr" 'l "Petrov"}]}
 ;; end::query-with-arguments4[]
 ))

(defn query-example-with-arguments-5 [node]
 (crux/q
  (crux/db node)
 ;; tag::query-with-arguments5[]
 {:find '[age]
  :where '[[(>= age 21)]]
  :args [{'age 22}]}
 ;; end::query-with-arguments5[]
 ))

#_(comment
  ;; tag::should-get[]
  #{[:dbpedia.resource/Pablo-Picasso]}
  ;; end::should-get[]

  ;; tag::should-get-entity[]
  {:crux.db/id :dbpedia.resource/Pablo-Picasso
   :name "Pablo"
   :last-name "Picasso"}
  ;; end::should-get-entity[]
  )
