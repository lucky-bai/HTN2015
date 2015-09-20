#!/bin/sh

gunicorn -w 4 -b 0.0.0.0:80 server:app
