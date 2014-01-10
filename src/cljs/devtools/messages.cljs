(ns messages
  (:require [goog.net.XhrIo :as xhr]))

(defrecord Message [message-type message-text])

(defn error-message
  [text]
  (Message. :error (str text)))

(defn warrning-message
  [text]
  (Message. :warning (str text)))

(defn regular-message
  [text]
  (cond
    (undefined? text) (Message. :regular "undefined")
    :else (Message. :regular (str text))))