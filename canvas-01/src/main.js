const canvas = document.getElementById("app");

const ctx = canvas.getContext("2d");

// Set display size (css pixels).
var size = 800;
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
    type: "block",
    sent: 0,
    collected: 0,
  },
  {
    id: "Block B",
    x: size/3,
    y: 2*size/4,
    radius: size/30,
    type: "block",
    color: "orange",
    sent: 0,
    collected: 0,
  },
  {
    id: "Block C",
    x: size/2,
    y: 3*size/4,
    radius: size/30,
    type: "block",
    color: "red",
    sent: 0,
    collected: 0,
  },
  {
    id: "Block D",
    x: size - size/3,
    y: 2*size/4,
    type: "block",
    radius: size/30,
    color: "pink",
    sent: 0,
    collected: 0,
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
let selecting = false;
let selectedItems = [];
let dragging= false;
let dragPos = { x: 0, y: 0 };

const drawText = (text, x, y, color="black") => {
  ctx.save();
  ctx.fillStyle = color;
  ctx.font = "16px Arial";
  ctx.textAlign = "center";
  ctx.fillText(text, x, y);
  ctx.restore();
}

function drawBlock(block) {
  let {x, y, radius, color} = block;
  drawCircle(x, y, radius, color);
  if (block.dragging) {
    drawCircle(x, y, radius * 1.2, "black");
  }
  // drawText(block.id, x, y);
  drawText(`⬆️${block.sent}`, x - radius, y - radius);
  drawText(`⬇️${block.collected}`, x + radius, y - radius);
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
    type: "parcels",
    life: 0.05,
    lifetime: 2.0,
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
      let from = getBlock(parcel.from);
      let to = getBlock(parcel.to);
      parcel.from = parcel.to;
      parcel.to = blocks[Math.floor(Math.random() * blocks.length)].id;
      parcel.color = from.color
      parcel.life = 0;
      from.sent += 1;
      to.collected += 1;
    }
    parcel.life += dt * speed;
  }
}

function drawSelection(dt) {
  if(selecting) {
    console.log(`moving selection: ${dragPos.x}, ${dragPos.y} to ${selectionPos.x}, ${selectionPos.y}`);
    ctx.save();
    ctx.beginPath();
    ctx.rect(dragPos.x, dragPos.y, (selectionPos.x - dragPos.x), (selectionPos.y - dragPos.y));
    ctx.lineWidth = 1;
    ctx.strokeStyle = "black";
    ctx.setLineDash([5, 5]);
    ctx.stroke();
    ctx.fillStyle = "rgba(0, 0, 0, 0.1)";
    ctx.fill();
    ctx.restore();
  }
  if (selectedItems.length > 0) {
    ctx.save();
    ctx.beginPath();
    xSorted = selectedItems.toSorted((a, b) => a.x - b.x)
    ySorted = selectedItems.toSorted((a, b) => a.y - b.y)
    ctx.rect(xSorted[0].x, ySorted[0].y, xSorted[xSorted.length - 1].x - xSorted[0].x, ySorted[ySorted.length - 1].y - ySorted[0].y)
    ctx.strokeStyle = "cyan";
    ctx.stroke();
    ctx.restore();
    ctx.fillStyle = "rgba(0, 0, 100, 0.1)";
    ctx.fill();
  }
}

function drawGrid() {
  ctx.save();
  ctx.beginPath();
  ctx.lineWidth = 1;
  ctx.strokeStyle = "black";
  ctx.setLineDash([10, 10]);
  for (let i = 0; i < size; i += size / 10) {
    ctx.moveTo(i, 0);
    ctx.lineTo(i, size);
    ctx.moveTo(0, i);
    ctx.lineTo(size, i);
  }
  ctx.stroke();
  ctx.restore();
}

function draw(dt) {
  setup();
  drawGraph();
  // drawGrid();
  drawSelection(dt);
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

canvas.addEventListener("mousedown", (e) => {
  let x = e.offsetX;
  let y = e.offsetY;
  console.log(`mousedown: ${x}, ${y}`);
  for (let block of blocks) {
    if (Math.abs(block.x - x) < block.radius && Math.abs(block.y - y) < block.radius) {
      dragging = true;
      block.dragging = true;
      break;
    }
  }
  if (!dragging) {
    selecting = true;
    dragPos = { x, y };
    selectionPos = { x, y };
  }
});

canvas.addEventListener("mousemove", (e) => {
  let x = e.offsetX;
  let y = e.offsetY;
  if (selecting) {
    selectionPos = { x, y };
  }
  // rubber band selection
  /* else if (dragging && isBetween(dragPos.x, x, selectionPos.x) && isBetween(dragPos.y, y, selectionPos.y)) {
   *   for(let item of selectedItems) {
   *     item.x = x;
   *     item.y = y;
   *   }
   *   selectionPos = {x, y};
   * } */
  if (dragging) {
    for (let block of blocks) {
      if (block.dragging) {
	block.x = x;
	block.y = y;
      }
    }
  }
});

const isBetween = (a, b, c) => {
  return (a <= b && b <= c) || (c <= b && b <= a);
}

const mouseRelease = (e) => {
  // dragPos = { x: 0, y: 0 };
  // selectionPos = { x: 0, y: 0 };
  selectedItems = [];
  if (dragging) {
    for (let block of blocks) {
      block.dragging = false;
    }
    dragging = false;
  }
  if (selecting) {
    for (let block of blocks) {
      if (isBetween(dragPos.x, block.x, selectionPos.x) && isBetween(dragPos.y, block.y, selectionPos.y)) {
        selectedItems.push(block);
      }
    }
    selecting = false;
  }
};

canvas.addEventListener("mouseup", (e) => {
  mouseRelease(e);
});

canvas.addEventListener("mouseleave", (e) => {
  mouseRelease(e);
});

ticker();
