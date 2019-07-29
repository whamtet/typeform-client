(ns typeform-client.client
  (:refer-clojure :exclude [get])
  (:require
    [clj-http.lite.client :as client]
    [clojure.data.json :as json]))

(def form-id "jnbFWS")
(def authorization (-> "authorization" slurp .trim))

(defn read-str [s]
  (json/read-str s :key-fn keyword))

(defn get []
  (->
    (client/get
      (format "https://api.typeform.com/forms/%s/responses" form-id)
      {:headers {"authorization" (format "bearer %s" authorization)}})
    :body
    read-str))
