(ns sekibomazic.file-upload-server
  (:gen-class)
  (:require  [org.httpkit.server :as server]
             [reitit.ring :as ring]
             [reitit.coercion.spec]
             [reitit.ring.coercion :as coercion]
             [reitit.ring.middleware.parameters :as parameters]
             [reitit.ring.middleware.multipart :as multipart]
             [clojure.java.io :as io]
             [clojure.pprint :as pprint]))


(defn home-routes []
  [["/upload"
    {:post {:summary "upload a file"
            :parameters {:multipart {:file multipart/temp-file-part}}
            :responses {200 {:body {:name string?, :size int?}}}
            :handler (fn [{{{:keys [file]} :multipart} :parameters :as request}]
                       (.mkdir (io/file "resources/uploads")) ;; For first run
                       (io/copy
                        (:tempfile file) ;; location of the downloaded file
                        (io/file (str "resources/uploads/" (:filename file))))
                       {:status 200
                        :headers {"Content-Type" "text/plain"}
                        :body {:name (:filename file)
                               :size (:size file)}})}}]
   ["/download"
    {:get {:summary "downloads a file"
           :swagger {:produces ["image/jpg"]}
           :handler (fn [_request]
                      {:status 200
                       :headers {"Content-Type" "image/jpg"}
                       :body (-> "scale.jpg"
                                 (io/resource)
                                 (io/input-stream))})}}]
   ])


(defn error-page [{:keys [status title]}]
  {:status status
   :headers {"Content-Type" "text/html"}
   :body (str "<h1>" status " " title "<h1>")})


(def app
  (ring/ring-handler
   (ring/router
    [(home-routes)]
    {:data {:coercion reitit.coercion.spec/coercion
            :middleware [
                         ;; query-params & form-params
                         parameters/parameters-middleware
                         ;; coercing response bodys
                         coercion/coerce-response-middleware
                         ;; coercing request parameters
                         coercion/coerce-request-middleware
                         ;; multipart
                         multipart/multipart-middleware
                         ]}})
   (ring/routes
    (ring/create-default-handler
     {:not-found
      (constantly (error-page {:status 404 :title "Page not found"}))
      :method-not-allowed
      (constantly (error-page {:status 405 :title "Not allowed"}))}))))


(def server (atom nil))


(defn stop-server
  "Gracefully shutdown the server, waiting 100ms"
  []
  (when-not (nil? @server)
    (println "INFO: Gracefully shutting down server...")
    (@server :timeout 100)
    (reset! server nil)))

(defn -main
  "Start a httpkit server with a specific port
  #' enables hot-reload of the handler function and anything that code calls"
  [& {:keys [ip port]
      :or   {ip   "0.0.0.0"
             port 8000}}]
  (println "INFO: Starting httpkit server - listening on: " (str "http://" ip ":" port))
  (reset! server (server/run-server #'app {:port port})))

