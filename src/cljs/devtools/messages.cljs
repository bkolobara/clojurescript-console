(ns messages
  (:require [goog.net.XhrIo :as xhr]))

(defrecord Message [message-type message-text])

(defn error-message
  [text]
  (Message. :error text))

(defn warrning-message
  [text]
  (Message. :warning text))

(defn regular-message
  [text]
  (Message. :regular text))