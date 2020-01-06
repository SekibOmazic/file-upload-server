# file-upload-server

A simple http-kit server with the reitit/ring library to demonstrate file upload and download

## Start from Emacs

Load file `file_upload_server.clj` and start REPL with `C-c M-j`
Switch the namespace `C-c M-n` and type `sekibomazic.file-upload-server`
From REPL execute `(-main)`

### File upload

From the console: `curl -F 'file=@path/to/some/file' http://localhost:8000/upload`

### File download

From the console: `curl http://localhost:8000`


## Installation

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

Run the project directly:

    $ clj -m sekibomazic.file-upload-server

Run the project's tests (they'll fail until you edit them):

    $ clj -A:test:runner

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2020 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
