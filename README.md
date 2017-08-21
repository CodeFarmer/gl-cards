# gl-cards
Monitor Gitlab CI pipelines and make red/green cards

## Building

Get Leiningen. You will need a JDK. Best to run using 'lein figwheel' for all
the hot reloading loveliness.

To build and run the docker image from scratch:

```bash
$ lein uberjar
$ docker build -t glc .
$ docker run --name glc -p8080:8080 -eGL_CARDS_GITLAB_PAT=INSERT_YOUR_ACCESS_TOKEN_HERE glc
```

Then point your browser at localhost:8080

## Code

* The UI is defined in src/cljs/gl_cards/core.cljs
* The main server is in src/clj/gl_cards/application.js
* Interface with the Gitlab API is in src/clj/gl_cards/core.js
* Stylesheet is resources/public/css/style.css

## Running

Expects a config.json file (sample provided) in the working directory; you will need a working Gitlab Personal Access Token in this in the GL_CARDS_GITLAB_PAT environment variable. First and only command-line argument is a port number, otherwise will default to 10555.
