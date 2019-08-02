(ns typeform-client.client
  (:refer-clojure :exclude [get])
  (:require
    [clojure.java.io :as io]
    [clj-http.lite.client :as client]
    [clojure.data.json :as json]))

(def form-id "jnbFWS")
(def authorization (-> "authorization" io/resource slurp .trim))

(defn read-str [s]
  (json/read-str s :key-fn keyword))

(defn get [page_size]
  (->
    (client/get
      (format "https://api.typeform.com/forms/%s/responses" form-id)
      {:headers {"authorization" (format "bearer %s" authorization)}
       :query-params {:page_size page_size}})
    :body
    read-str))
