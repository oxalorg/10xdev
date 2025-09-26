(ns raylib
  (:import [com.raylib Raylib Camera3D Vector3]
           [com.raylib Raylib$CameraMode Raylib$CameraProjection]))

(defn -main [& args]
  ;; Initialize window
  (println "Running main")
  (let [window
        (future
          (Raylib/initWindow 800 450 "Demo"))]
    (Raylib/setTargetFPS 60)

    ;; Create 3D camera
    (let [camera (Camera3D. (Vector3. 18 16 18) ; position
                            (Vector3. 0 0 0)    ; target
                            (Vector3. 0 1 0)    ; up vector
                            45                  ; fov
                            Raylib$CameraProjection/CAMERA_PERSPECTIVE)]

      ;; Main game loop
      (loop []
        (when-not (Raylib/windowShouldClose)
          ;; Update camera
          (Raylib/updateCamera camera Raylib$CameraMode/CAMERA_ORBITAL)

          ;; Begin drawing
          (Raylib/beginDrawing)
          (Raylib/clearBackground Raylib/RAYWHITE)

          ;; 3D drawing
          (Raylib/beginMode3D camera)
          (Raylib/drawGrid 20 1.0)
          (Raylib/endMode3D)

          ;; 2D drawing
          (Raylib/drawText "Hello world" 190 200 20 Raylib/VIOLET)
          (Raylib/drawFPS 20 20)

          (Raylib/endDrawing)

          ;; Continue loop
          (recur)))

      (println "Cleanup")
      ;; Cleanup
      (Raylib/closeWindow)
      )
    ))

(-main)

(+ 1 12)

(def window (future (Raylib/initWindow 900 405 "window demo")))

@window
(Raylib/windowShouldClose)


(defn quick-test []
  (future
    (Raylib/initWindow 400 300 "Quick Test")
    (Raylib/setTargetFPS 60)

    ;; Run for just a few seconds then close
    (dotimes [_ 180]  ; 3 seconds at 60 FPS
      (when (Raylib/windowShouldClose) (reduced nil))
      (Raylib/beginDrawing)
      (Raylib/clearBackground Raylib/RAYWHITE)
      (Raylib/drawText "Quick Test!" 50 50 20 Raylib/BLACK)
      (Raylib/endDrawing))

    (Raylib/closeWindow)))

;; Usage: (quick-test)

(defn minimal-test []
  (println "Creating thread...")
  (let [t (Thread.
           (fn []
             (println "Thread started, initializing window...")
             (Raylib/initWindow 300 200 "Minimal")
             (println "Window created, starting loop...")
             (dotimes [i 60]  ; 1 second at 60 FPS
               (Raylib/beginDrawing)
               (Raylib/clearBackground Raylib/WHITE)
               (Raylib/endDrawing)
               (Thread/sleep 16))  ; ~60 FPS
             (println "Loop done, closing...")
             (Raylib/closeWindow)
             (println "Done!")))]
    (.start t)
    (println "Thread started, returning...")))

(minimal-test)
(defn diagnose []
  (println "=== Raylib Diagnosis ===")
  (println "Java version:" (System/getProperty "java.version"))
  (println "OS:" (System/getProperty "os.name") (System/getProperty "os.arch"))
  (println "Current thread:" (.getName (Thread/currentThread)))

  ;; Check if classes exist
  (try
    (println "Raylib class found:" (not (nil? com.raylib.Raylib)))
    (catch Exception e (println "Raylib class not found:" e)))

  ;; Check native library loading
  (try
    (println "Attempting to call Raylib method...")
    ;; This might fail but will tell us more
    (Raylib/getTime)  ; This should work without a window
    (println "Basic Raylib call succeeded!")
    (catch Exception e
      (println "Raylib call failed:" (.getMessage e)))))

(diagnose)
