(ns typeform-client.core
  (:require
    [typeform-client.client :as client]
    [typeform-client.db :as db]
    [typeform-client.excel :as excel]
    [clojure.set :as set])
  (:gen-class))

(def answer-ids
  (zipmap
    '("62396569" "62396798" "60249902" "67562566" "62393978" "62393676" "60249963" "60249998")
    [:first-name :last-name :procedures :motivation :financing :concerns :email :phone]))

(defn answer [{:keys [text email choices field]}]
  [(-> field :id answer-ids)
   (or text email (-> choices :labels set))])

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

(defn item->row [{:keys [answers]}]
  (let [{:keys [first-name last-name procedures motivation financing concerns email phone]}
        (into {} (map answer answers))]
    {"Name" [first-name last-name]
     "Email" [email]
     "Phone Number" [phone]
     "Procedure" (map (or procedures #{}) all-procedures)
     "Financing Required" (map (or financing #{}) ["Yes" "No"])
     "Primary Concerns/Questions" (map (or concerns #{}) all-concerns)
     "Primary Motivation" [motivation]}))

(defn read-num [s default]
  (println (format "%s (default %s)" s default))
  (try
    (-> (read-line) .trim Integer/parseInt)
    (catch NumberFormatException e default)))

(defn -main [& args]
  (let [master-size (read-num "Num of master leads" 8)
        page-size (read-num "Num of typeform responses" 25)
        completed (db/completed)
        completed? #(-> "Email" % first completed)]
    (excel/mod-sheet
      "DM-Master-Lead-List.xlsx"
      (fn [master-leads]
        (let [master-leads (->> master-leads shuffle (remove completed?) (take master-size))
              new-leads (->> page-size client/get :items (map item->row) (remove completed?))
              leads (concat master-leads new-leads)
              emails (map #(-> "Email" % first) leads)]
          (db/completed (set/union completed (set emails)))
          leads))
      "New Leads.xlsx")))
