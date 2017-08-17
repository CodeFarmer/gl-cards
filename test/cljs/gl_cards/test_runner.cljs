(ns gl-cards.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [gl-cards.core-test]
   [gl-cards.common-test]))

(enable-console-print!)

(doo-tests 'gl-cards.core-test
           'gl-cards.common-test)
