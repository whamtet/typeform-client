(ns typeform-client.core
  (:require
    [typeform-client.client :as client]
    [typeform-client.db :as db]))

(defonce x (client/get))
(def i (-> x :items first))

(defn answer [{:keys [text email choices]}]
  (or text email (-> choices :labels set)))

(def all-procedures
  ["Gastric Sleeve"
   "Gastric Bypass"
   "Duodenal Switch"
   "Gastric Band"
   "Revision"])

(def all-concerns
  ["Quality of Surgeon"
   "Quality of Facility"
   "Location"
   "Postop Care"
   "Postop Complications"
   "Specific Medical Questions"])

(defn item->row [item]
  (let [[first-name last-name procedures motivation financing concerns email phone]
        (map answer (:answers item))]
    {"Name" [first-name last-name]
     "Email" [email]
     "Phone Number" phone
     "Procedure" (map procedures all-procedures)
     "Financing Required" (map financing ["Yes" "No"])
     "Primary Concerns/Questions" (map concerns all-concerns)
     "Primary Motivation" [motivation]}))

(defn read-num
  ([s] (read-num s Integer/MAX_VALUE))
  ([s max]
   (print s "")
   (.flush *out*)
   (try
     (let [num (-> (read-line) .trim Integer/parseInt)]
       (if (or (< num 1) (< max num))
         (do
           (printf "Enter number between %s and %s\n" 1 max)
           (read-num s max))
         num))
     (catch NumberFormatException e
       (println "Enter valid number")
       (read-num s max)))))
