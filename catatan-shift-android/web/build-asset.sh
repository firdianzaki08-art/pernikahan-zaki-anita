#!/usr/bin/env bash
# Wraps the page (same source as the web version) into a full HTML document for the app's WebView.
set -euo pipefail
cd "$(dirname "$0")"
out=../app/src/main/assets/index.html
mkdir -p "$(dirname "$out")"
{
  printf '%s' '<!doctype html><html lang="id"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1,viewport-fit=cover">'
  printf '%s' '<style>:root{color-scheme:light;padding-top:env(safe-area-inset-top);padding-bottom:env(safe-area-inset-bottom)}body{margin:0;font:14px system-ui,sans-serif}img{max-width:100%}[hidden]{display:none!important}</style></head><body>'
  cat catatan-shift.html
  printf '%s' '</body></html>'
} > "$out"
echo "wrote $out"
