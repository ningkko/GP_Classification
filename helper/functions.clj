;; gorilla-repl.fileformat = 1

;; **
;;; # ast functions
;;; 
;;; 
;;; 
;; **

;; @@
(ns functions
  (:require [gorilla-plot.core :as plot]
   			[propel.core :refer :all]
   			[clojure-csv.core :refer :all]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(require '[clojure.data.csv :as csv]
         '[clojure.java.io :as io])
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(def filename "src/training_set_metadata.csv")
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;functions/filename</span>","value":"#'functions/filename"}
;; <=

;; @@
(defn read-file
  [filename]
  (doall
    (with-open [reader (io/reader filename)]
      (csv/read-csv reader))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;functions/read-file</span>","value":"#'functions/read-file"}
;; <=

;; @@
(defn read-column 
  [filename column-index]
  (with-open [reader (io/reader filename)]
    (let [data (csv/read-csv reader)]
      (doall
        (map #(nth % column-index) data)))))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;functions/read-column</span>","value":"#'functions/read-column"}
;; <=

;; @@
(defn read-row 
  [filename row-index]
  (with-open [reader (io/reader filename)]
      (nth (csv/read-csv reader) row-index)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;functions/read-row</span>","value":"#'functions/read-row"}
;; <=

;; @@
;;(def input (rest (map #(drop-last (read-row filename %)) (range (count (read-column filename 0))))))
(defn to-float 
  [input]
  (let [evaluated-input (read-string input)]
    (if (= clojure.lang.Symbol (type evaluated-input))
      (float 0.0)
      (float evaluated-input))))

(map to-float (drop-last (read-row filename 1)))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-unkown'>615.0</span>","value":"615.0"},{"type":"html","content":"<span class='clj-unkown'>349.04605</span>","value":"349.04605"},{"type":"html","content":"<span class='clj-unkown'>-61.943836</span>","value":"-61.943836"},{"type":"html","content":"<span class='clj-unkown'>320.79654</span>","value":"320.79654"},{"type":"html","content":"<span class='clj-unkown'>-51.753708</span>","value":"-51.753708"},{"type":"html","content":"<span class='clj-unkown'>1.0</span>","value":"1.0"},{"type":"html","content":"<span class='clj-unkown'>0.0</span>","value":"0.0"},{"type":"html","content":"<span class='clj-unkown'>0.0</span>","value":"0.0"},{"type":"html","content":"<span class='clj-unkown'>0.0</span>","value":"0.0"},{"type":"html","content":"<span class='clj-unkown'>0.0</span>","value":"0.0"},{"type":"html","content":"<span class='clj-unkown'>0.017</span>","value":"0.017"}],"value":"(615.0 349.04605 -61.943836 320.79654 -51.753708 1.0 0.0 0.0 0.0 0.0 0.017)"}
;; <=

;; @@

(defn get-input
  []
  (rest (map #(map to-float (drop-last (read-row filename %))) 
             (range (count (read-column filename 0))))))

(def input (get-input))
input

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;functions/input</span>","value":"#'functions/input"}
;; <=

;; @@
;;( type 
;;	(read-column "src/training_set_metadata.csv" 11))

(defn get-target-data
  [file-name target-column]
  (rest 
    (apply vector 
           (read-column file-name target-column))))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;functions/get-target-data</span>","value":"#'functions/get-target-data"}
;; <=

;; @@
(def data-names ["object_id" "ra" "decl" "gal_l" "gal_b" "ddf" "hostgal_specz" "hostgal_photoz" "hostgal_photoz_err" "distmod" "mwebv" "target"])
(nth data-names 1)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-string'>&quot;ra&quot;</span>","value":"\"ra\""}
;; <=

;; @@
;;infinite loop
;;(map #(flatten (list (read-row filename %))) (map inc (range (dec (count (read-column filename 0))))))
;; @@

;; @@
(defn tournament-selection-revised
  "Elements are sorted according to their error first and then the first half will be taken. After which 1/10 of them will be selected"
  [pop]
  (let [half-size (/ (count pop) 2)
        tournament-set (take half-size (apply min-key :total-error pop))
        tournament-size (/ (count tournament-set) 10)]
        (take tournament-size (shuffle pop))
    ))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;functions/tournament-selection-revised</span>","value":"#'functions/tournament-selection-revised"}
;; <=

;; @@
(defn uniform-crossover
  "Crosses over two individuals using uniform crossover. Pads shorter one."
  [plushy-a plushy-b]
  (let [shorter (min-key count plushy-a plushy-b)
        longer (if (counted? plushy-a);;choose a quicker way to set longer&shorter
                 (max-key count plushy-b plushy-a)
                 (if (= shorter plushy-a)
                   plushy-b
                   plushy-a))
        
        length-diff (- (count longer) (count shorter))
        shorter-padded (concat shorter (repeat length-diff :crossover-padding))]
    
    (remove #(= % :crossover-padding)
            (map #(if (< (rand) 0.5) %1 %2)
                 shorter-padded
                 longer))))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;functions/uniform-crossover</span>","value":"#'functions/uniform-crossover"}
;; <=

;; @@
(defn multi-point-crossover-interleaving
  "Multi point crossover is a generalization of the one-point crossover wherein alternating segments are swapped to get new off-springs...
  take odd genomes, uniform sized
  a-1+b-2+a-3+b-4+...+a_left+b_left"
  
  [plushy-a plushy-b]
  (let [shorter (min-key count plushy-a plushy-b)
        longer (if (= shorter plushy-a)
                   plushy-b
                   plushy-a)
        length (count longer) ;;length of genes
        ;;at least 2 chunks'
        chunk-number (+ 2 (rand-int (dec length)))
        chunk-size (int (/ length chunk-number))
        length-diff (- (count longer) (count shorter))
        shorter-padded (concat shorter (repeat length-diff :crossover-padding))
        segmented-a (map vec (partition-all chunk-size plushy-a))
        segmented-b (map vec (partition-all chunk-size plushy-b))]  
    
    (loop [use-a (rand-nth [true false])
           a segmented-a
           b segmented-b
           result []]
        
      (if (empty? a)
        (remove #(= % :crossover-padding) result)
        (recur (not use-a)
               (rest a)
               (rest b)
               (concat result (if use-a
                              (first a) 
                              (first b)))))))) 



(defn multi-point-crossover-parallel
  "a1-b1-a3-b3-... or a-2-b2-a4-b4-..."
  [plushy-a plushy-b]
  (let [shorter (min-key count plushy-a plushy-b)
        longer (if (= shorter plushy-a)
                   plushy-b
                   plushy-a)
        length (count longer) ;;length of genes
        chunk-number (+ 1 (rand-int length))
        chunk-size (int (/ length chunk-number))
        length-diff (- (count longer) (count shorter))
        shorter-padded (concat shorter (repeat length-diff :crossover-padding))
        segmented-a (map vec (partition-all chunk-size plushy-a))
        segmented-b (map vec (partition-all chunk-size plushy-b))]
    
    (loop [start-at-0th (rand-nth [true false])
           a (if start-at-0th
               segmented-a
               (rest segmented-a))
           b (if start-at-0th
               segmented-b
               (rest segmented-b))
           result []]
        
      (if (empty? a)
        (remove #(= % :crossover-padding) result)
        (recur start-at-0th
               (rest (rest a))
               (rest (rest b))
               (concat result (first a) (first b))))))) 

   


;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;functions/multi-point-crossover-parallel</span>","value":"#'functions/multi-point-crossover-parallel"}
;; <=

;; @@
(comment
"Bit string mutation
The mutation of bit strings ensue through bit flips at random positions.

This mutation operator takes the chosen genome and inverts the bits (i.e. if the genome bit is 1, it is changed to 0 and vice versa).

Non-Uniform
The probability that amount of mutation will go to 0 with the next generation is increased by using non-uniform mutation operator. It keeps the population from stagnating in the early stages of the evolution. It tunes solution in later stages of evolution. This mutation operator can only be used for integer and float genes.

Uniform
This operator replaces the value of the chosen gene with a uniform random value selected between the user-specified upper and lower bounds for that gene. This mutation operator can only be used for integer and float genes.

Gaussian
This operator adds a unit Gaussian distributed random value to the chosen gene. If it falls outside of the user-specified lower or upper bounds for that gene, the new gene value is clipped. This mutation operator can only be used for integer and float genes.

Shrink
This operator adds a random number taken from a Gaussian distribution with mean equal to the original value of each decision variable characterizing the entry parent vector.")
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(defn bit-mutation
  "see definition above.bMutation rate [0 1)"
  [plushy mutation-rate]
  (map #(if (<= (rand) mutation-rate)
             (rand-nth instructions)
             %) 
       plushy))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;functions/bit-mutation</span>","value":"#'functions/bit-mutation"}
;; <=

;; @@
(defn get-target
  [file-name]
  (doall
    (map #(float (read-string %))
         (rest (read-column file-name 11)))))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;functions/get-target</span>","value":"#'functions/get-target"}
;; <=

;; @@
;;(get-target filename)
;; @@
