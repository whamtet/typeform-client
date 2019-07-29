(ns typeform-client.db
  (:require
    [typeform-client.excel :as excel])
  (:import
    java.io.File))

(def f (File. "completed"))

(defn completed
  ([]
   (when-not (.exists f)
     (try
       (->> "Pompeii-Leads-Master-List.xlsx"
            excel/parse-sheet
            :rows
            (map #(-> "Email" % first))
            (filter not-empty)
            set
            completed)))
   (-> f
       slurp
       read-string))
  ([s]
   (->> s
        pr-str
        (spit f))))
