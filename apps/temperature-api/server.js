const http = require("http");

const PORT = Number(process.env.PORT) || 8081;

const locationBySensorId = {
  1: "Living Room",
  2: "Bedroom",
  3: "Kitchen",
};

const sensorIdByLocation = {
  "Living Room": "1",
  Bedroom: "2",
  Kitchen: "3",
};

function resolveLocationAndSensor(location, sensorId) {
  if (location === "") {
    location = locationBySensorId[sensorId] || "Unknown";
  }
  if (sensorId === "") {
    sensorId = sensorIdByLocation[location] || "0";
  }
  return { location, sensorId };
}

function randomTemperature() {
  return Math.round((10 + Math.random() * 20) * 10) / 10;
}

function sendJson(res, status, body) {
  res.writeHead(status, { "Content-Type": "application/json" });
  res.end(JSON.stringify(body));
}

const server = http.createServer((req, res) => {
  const url = new URL(req.url, "http://localhost");
  const parts = url.pathname.split("/").filter(Boolean);

  if (req.method === "GET" && parts[0] === "health") {
    sendJson(res, 200, { status: "ok" });
    return;
  }

  if (req.method !== "GET" || parts[0] !== "temperature") {
    sendJson(res, 404, { error: "not found" });
    return;
  }

  let location = url.searchParams.get("location") || "";
  let sensorId =
    url.searchParams.get("sensorId") || url.searchParams.get("sensor_id") || "";
  if (parts[1] && sensorId === "") {
    sensorId = decodeURIComponent(parts[1]);
  }

  const resolved = resolveLocationAndSensor(location, sensorId);
  sendJson(res, 200, {
    value: randomTemperature(),
    unit: "°C",
    timestamp: new Date().toISOString(),
    location: resolved.location,
    status: "active",
    sensor_id: resolved.sensorId,
    sensor_type: "temperature",
    description: `Temperature in ${resolved.location}`,
  });
});

server.listen(PORT, "0.0.0.0", () => {
  console.log(`temperature-api listening on ${PORT}`);
});
