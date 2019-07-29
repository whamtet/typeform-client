(ns typeform-client.excel
  (:require
    [dk.ative.docjure.spreadsheet :as xls]))

(defn cell-seq [row]
  (->> row xls/cell-seq (map str)))

(defn read-sheet [sheet]
  (->> sheet
       xls/load-workbook
       xls/sheet-seq
       first
       xls/row-seq
       (map cell-seq)))

(defn repeat-non-empty [s]
  (reduce
    #(conj %1 (if (empty? %2) (peek %1) %2))
    []
    s))

(defn conj-v [v x]
  (conj (or v []) x))

(defn title-row [title row]
  (reduce
    (fn [m [t v]]
      (update m t conj-v v))
    {}
    (map list title row)))

(defn parse-sheet [f]
  (let [[title & rows] (read-sheet f)
        title-repeat (repeat-non-empty title)]
    {:title title :rows (map #(title-row title-repeat %) rows)}))

(defn spit-sheet [f {:keys [title rows]}]
  (let [ks (filter not-empty title)]
    (->>
      (for [row rows]
        (mapcat row ks))
      (cons title)
      (xls/create-workbook "Contacts")
      (xls/save-workbook! f))))
