import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

devices = []


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path.startswith("/health"):
            self._send(200, {"status": "ok"})
            return
        if self.path.startswith("/devices"):
            self._send(200, devices)
            return
        self._send(404, {"error": "not found"})

    def do_POST(self):
        if not self.path.startswith("/devices"):
            self._send(404, {"error": "not found"})
            return
        length = int(self.headers.get("Content-Length", "0"))
        raw = self.rfile.read(length) if length else b"{}"
        device = json.loads(raw.decode() or "{}")
        devices.append(device)
        self._send(201, device)

    def _send(self, status, body):
        data = json.dumps(body).encode()
        self.send_response(status)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)

    def log_message(self, fmt, *args):
        print(fmt % args)


if __name__ == "__main__":
    print("devices listening on 8082")
    ThreadingHTTPServer(("0.0.0.0", 8082), Handler).serve_forever()
