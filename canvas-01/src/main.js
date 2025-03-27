const canvas = document.getElementById("app");

const ctx = canvas.getContext("2d");

// Set display size (css pixels).
var size = 1300;
function setup() {
  canvas.style.width = size + "px";
  canvas.style.height = size + "px";

  // Set actual size in memory (scaled to account for extra pixel density).
  var scale = window.devicePixelRatio; // Change to 1 on retina screens to see blurry canvas.
  canvas.width = size * scale;
  canvas.height = size * scale;

  // Normalize coordinate system to use css pixels.
  ctx.scale(scale, scale);
}

const drawCircle = (x, y, radius, color="black") => {
  ctx.save();
  ctx.beginPath();
  ctx.lineWidth = 5;
  ctx.strokeStyle = color;
  ctx.arc(x, y, radius, 0, 2 * Math.PI);
  ctx.stroke();
  ctx.restore();
}

const drawLine = (x1, y1, x2, y2) => {
  ctx.save();
  ctx.strokeStyle = "black";
  ctx.lineWidth = 2;
  ctx.setLineDash([1, 5]);
  ctx.beginPath();
  ctx.moveTo(x1, y1);
  ctx.lineTo(x2, y2);
  ctx.stroke();
  ctx.restore();
}

const drawTriangle = (x, y, size, color="black") => {
  ctx.save();
  ctx.strokeStyle = color;
  ctx.fillStyle = color;
  ctx.beginPath();
  ctx.moveTo(x, y);
  ctx.lineTo(x + size/2, y + size/2);
  ctx.lineTo(x - size/2, y + size/2);
  ctx.fill();
  ctx.closePath();
  ctx.stroke();
  ctx.restore();
}

let previousFrame = Date.now()
let blocks = [
  {
    id: "Block A",
    x: size/2,
    y: size/4,
    radius: size/30,
    color: "green",
  },
  {
    id: "Block B",
    x: size/3,
    y: 2*size/4,
    radius: size/30,
    color: "orange",
  },
  {
    id: "Block C",
    x: size/2,
    y: 3*size/4,
    radius: size/30,
    color: "red",
  },
  {
    id: "Block D",
    x: size - size/3,
    y: 2*size/4,
    radius: size/30,
    color: "pink",
  }
]
function getBlock(id) {
  for (const block of blocks) {
    if (block.id == id) {
      return block;
    }
  }
}

let parcels = [];

function drawBlock(block) {
  let {x, y, radius, color} = block;
  drawCircle(x, y, radius, color);
}

function drawGraph() {
  for (const block of blocks) {
    for (const block2 of blocks) {
      if(block.id != block2.id) {
        drawLine(block2.x, block2.y, block.x, block.y)
      }
    }
    drawBlock(block);
  }
}


for(let i = 0; i < 10; i++) {
  parcels.push({
    id: i,
    from: blocks[0].id,
    to: blocks[1].id,
    color: blocks[0].color,
    life: 0.05,
    lifetime: 1.0,
    speed: 1 + Math.random(),
  })
}

function drawParcel(parcel, dt = 0.01) {
  let { life, lifetime, color, id, speed } = parcel;
  let from, to;
  for (let block of blocks) {
    if (block.id == parcel.from) {
      from = block;
    }
    if (block.id == parcel.to) {
      to = block;
    }
  }
  let travelled = life / lifetime;
  drawTriangle(
    (from.x + (to.x - from.x) * travelled),
    (from.y + (to.y - from.y) * travelled),
    20,
    color=parcel.color
  );
}

function flow(dt) {
  for (let parcel of parcels) {
    let { life, lifetime, speed } = parcel;
    if (life > lifetime) {
      parcel.from = parcel.to;
      parcel.to = blocks[Math.floor(Math.random() * blocks.length)].id;
      parcel.color = getBlock(parcel.from).color;
      parcel.life = 0;
    }
    parcel.life += dt * speed;
  }
}

function draw(dt) {
  setup();
  drawGraph();
  
  for (let parcel of parcels) {
    drawParcel(parcel, dt);
  }
}

function ticker() {
  const currentFrame = Date.now()
  const dt = (currentFrame - previousFrame) / 1000;
  previousFrame = currentFrame
  draw(dt)
  flow(dt)
  requestAnimationFrame(ticker)
}

ticker();