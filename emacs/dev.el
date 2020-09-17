(setq cider-default-cljs-repl 'shadow)
(setq cider-shadow-cljs-default-options "app")

(defun pas/rerender ()
  (interactive)
  (cider-nrepl-sync-request:eval "(pluggable-web.core/remount-root)"))

(defun pas/reagent-rerender (orig-fun &rest args)
  (let ((res (apply orig-fun args)))
    (sleep-for 0.1)
    (cider-nrepl-sync-request:eval "(pluggable-web.core/remount-root)")
    res))

(advice-add 'cider-eval-defun-at-point :around #'pas/reagent-rerender)
(advice-add 'cider-eval-buffer         :around #'pas/reagent-rerender)
(advice-add 'cider-eval-region         :around #'pas/reagent-rerender)

(define-clojure-indent
  (>defn 1))

(super-save-stop) ;; FIXME: check that this works as intended
