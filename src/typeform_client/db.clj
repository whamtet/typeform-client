(ns typeform-client.db
  (:import
    java.io.File))

(def f (File. "completed"))

(defn completed
  ([]
   (if (.exists f)
     (-> f
         slurp
         read-string)
     #{}))
  ([s]
   (->> s
        pr-str
        (spit "completed"))))
