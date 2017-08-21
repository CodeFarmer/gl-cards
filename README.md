# gl-cards
Monitor Gitlab CI pipelines and make red/green cards

* Building

Get Leiningen. You will need a JDK. Best to run using 'lein figwheel' for all
the hot reloading loveliness.

* Running

Expects a config.json file (sample privided; you will need a working Gitlab Personal Access Token). First and only command-line argument is a port number, otherwise will default to 10555.
