import Konva from 'konva';

// Initialize the stage and layer
const stage = new Konva.Stage({
  container: 'canvas-container',
  width: window.innerWidth,
  height: window.innerHeight,
});

const layer = new Konva.Layer();
stage.add(layer);

// Set up the stage to be responsive
window.addEventListener('resize', () => {
  stage.width(window.innerWidth);
  stage.height(window.innerHeight);
  layer.draw();
});

// Node dimensions
const NODE_WIDTH = 300;
const NODE_HEIGHT = 100;
const HANDLE_RADIUS = 8;

// Track nodes and connections
const nodes = [];
const connections = [];

// Connection creation state
let connectionState = {
  isCreating: false,
  tempLine: null,
  sourceNode: null,
  sourceHandle: null
};

// Function to create a new node
function createNode(x, y) {
  const nodeGroup = new Konva.Group({
    x: x,
    y: y,
    draggable: true,
  });

  // Create the rectangle
  const rect = new Konva.Rect({
    width: NODE_WIDTH,
    height: NODE_HEIGHT,
    fill: '#ffffff',
    stroke: '#000000',
    strokeWidth: 2,
    cornerRadius: 5,
  });

  // Add a text label
  const text = new Konva.Text({
    text: `Node ${nodes.length + 1}`,
    fontSize: 16,
    fill: '#000000',
    align: 'center',
    verticalAlign: 'middle',
    width: NODE_WIDTH,
    height: NODE_HEIGHT,
  });

  // Create connection handles
  const handles = [];
  const handlePositions = [
    { x: NODE_WIDTH / 2, y: 0 }, // top
    { x: NODE_WIDTH, y: NODE_HEIGHT / 2 }, // right
    { x: NODE_WIDTH / 2, y: NODE_HEIGHT }, // bottom
    { x: 0, y: NODE_HEIGHT / 2 }, // left
  ];

  handlePositions.forEach((pos, index) => {
    const handle = new Konva.Circle({
      x: pos.x,
      y: pos.y,
      radius: HANDLE_RADIUS,
      fill: '#4a90e2',
      stroke: '#2171c7',
      strokeWidth: 2,
      // Make handles not draggable
      draggable: false,
    });

    // Store the handle index
    handle.handleIndex = index;

    // Add hover effect
    handle.on('mouseover', () => {
      handle.fill('#2171c7');
      layer.draw();
    });

    handle.on('mouseout', () => {
      handle.fill('#4a90e2');
      layer.draw();
    });

    // Add click event for connection creation
    handle.on('mousedown', (e) => {
      // Stop event propagation
      e.cancelBubble = true;
      
      // Create a temporary line for the connection being created
      const tempLine = new Konva.Line({
        points: [
          handle.x() + nodeGroup.x(),
          handle.y() + nodeGroup.y(),
          handle.x() + nodeGroup.x(),
          handle.y() + nodeGroup.y(),
        ],
        stroke: '#000000',
        strokeWidth: 2,
        dash: [5, 5],
      });
      
      layer.add(tempLine);
      
      // Store the connection state
      connectionState = {
        isCreating: true,
        tempLine: tempLine,
        sourceNode: nodeGroup,
        sourceHandle: handle
      };
      
      // Highlight potential target nodes
    //   nodes.forEach(node => {
    //     if (node.group !== nodeGroup) {
    //       node.group.opacity(0.7);
    //     }
    //   });
      
      // Add mousemove and mouseup events to the stage
      stage.on('mousemove', handleMouseMove);
      stage.on('mouseup', handleMouseUp);
      
      layer.draw();
    });

    handles.push(handle);
    nodeGroup.add(handle);
  });

  // Add the rectangle and text to the group
  nodeGroup.add(rect);
  nodeGroup.add(text);

  // Add transformers for scaling and rotating
  const transformer = new Konva.Transformer({
    nodes: [nodeGroup],
    boundBoxFunc: (oldBox, newBox) => {
      // Limit the minimum size
      if (newBox.width < 50 || newBox.height < 50) {
        return oldBox;
      }
      return newBox;
    },
  });

  // Add the group and transformer to the layer
  layer.add(nodeGroup);
  layer.add(transformer);

  // Store the node and its transformer
  nodes.push({
    group: nodeGroup,
    transformer: transformer,
  });

  // Make the node draggable
  nodeGroup.on('dragmove', () => {
    updateConnections();
  });

  // Make the node transformable
  nodeGroup.on('transform', () => {
    updateConnections();
  });

  // Show transformer on click
  nodeGroup.on('click', (e) => {
    // Stop event propagation to prevent stage click from firing
    e.cancelBubble = true;
    
    // Hide all other transformers
    nodes.forEach(node => {
      if (node.transformer !== transformer) {
        node.transformer.nodes([]);
      }
    });
    
    // Show this transformer
    transformer.nodes([nodeGroup]);
    layer.draw();
  });

  // Activate transformer on double-click
  nodeGroup.on('dblclick', (e) => {
    // Stop event propagation to prevent stage dblclick from firing
    e.cancelBubble = true;
    
    // Hide all other transformers
    nodes.forEach(node => {
      if (node.transformer !== transformer) {
        node.transformer.nodes([]);
      }
    });
    
    // Show this transformer
    transformer.nodes([nodeGroup]);
    
    // Enable rotation and scaling
    transformer.enabledAnchors([
      'top-left', 'top-center', 'top-right',
      'middle-left', 'middle-right',
      'bottom-left', 'bottom-center', 'bottom-right',
      'rotater'
    ]);
    
    layer.draw();
  });

  // Update the layer
  layer.draw();
  return nodeGroup;
}

// Handle mouse move during connection creation
function handleMouseMove(e) {
  console.log("Mouse moving")
  if (connectionState.isCreating && connectionState.tempLine) {
    const pos = stage.getPointerPosition();
    connectionState.tempLine.points([
      connectionState.sourceHandle.x() + connectionState.sourceNode.x(),
      connectionState.sourceHandle.y() + connectionState.sourceNode.y(),
      pos.x,
      pos.y,
    ]);
    
    // Only redraw the layer, not the entire stage
    layer.batchDraw();
  }
}

// Handle mouse up during connection creation
function handleMouseUp(e) {
  if (connectionState.isCreating) {
    // Remove the temporary line
    if (connectionState.tempLine) {
      connectionState.tempLine.destroy();
    }
    
    // Reset node opacity
    nodes.forEach(node => {
      node.group.opacity(1);
    });
    
    // Check if we're over another node
    const pos = stage.getPointerPosition();
    const targetNode = findNodeAtPosition(pos.x, pos.y);
    
    if (targetNode && targetNode !== connectionState.sourceNode) {
      // Create a connection
      createConnection(connectionState.sourceNode, targetNode);
    }
    
    // Remove event listeners
    stage.off('mousemove', handleMouseMove);
    stage.off('mouseup', handleMouseUp);
    
    // Reset connection state
    connectionState = {
      isCreating: false,
      tempLine: null,
      sourceNode: null,
      sourceHandle: null
    };
    
    layer.batchDraw();
  }
}

// Function to find a node at a specific position
function findNodeAtPosition(x, y) {
  for (const node of nodes) {
    const nodeGroup = node.group;
    const nodeX = nodeGroup.x();
    const nodeY = nodeGroup.y();
    
    if (
      x >= nodeX &&
      x <= nodeX + NODE_WIDTH &&
      y >= nodeY &&
      y <= nodeY + NODE_HEIGHT
    ) {
      return nodeGroup;
    }
  }
  return null;
}

// Function to create a connection between two nodes
function createConnection(startNode, endNode) {
  // Create a line
  const line = new Konva.Line({
    points: [
      startNode.x() + NODE_WIDTH / 2,
      startNode.y() + NODE_HEIGHT / 2,
      endNode.x() + NODE_WIDTH / 2,
      endNode.y() + NODE_HEIGHT / 2,
    ],
    stroke: '#000000',
    strokeWidth: 2,
    pointerLength: 10,
    pointerWidth: 10,
  });

  // Add the line to the layer
  layer.add(line);

  // Store the connection
  connections.push({
    line: line,
    startNode: startNode,
    endNode: endNode,
  });

  // Update the layer
  layer.draw();
}

// Function to update all connections
function updateConnections() {
  connections.forEach(connection => {
    const startNode = connection.startNode;
    const endNode = connection.endNode;
    const line = connection.line;

    // Update the line points
    line.points([
      startNode.x() + NODE_WIDTH / 2,
      startNode.y() + NODE_HEIGHT / 2,
      endNode.x() + NODE_WIDTH / 2,
      endNode.y() + NODE_HEIGHT / 2,
    ]);
  });

  // Update the layer
  layer.draw();
}

// Add a node when the "Add Node" button is clicked
document.getElementById('add-node').addEventListener('click', () => {
  // Create a node at a random position
  const x = Math.random() * (stage.width() - NODE_WIDTH);
  const y = Math.random() * (stage.height() - NODE_HEIGHT);
  createNode(x, y);
});

// Add a connection when the "Add Connection" button is clicked
let isAddingConnection = false;
let startNodeForConnection = null;

document.getElementById('add-connection').addEventListener('click', () => {
  if (!isAddingConnection) {
    isAddingConnection = true;
    document.getElementById('add-connection').textContent = 'Select End Node';
    
    // Add click event to all nodes
    nodes.forEach(node => {
      node.group.on('click', () => {
        if (isAddingConnection) {
          if (!startNodeForConnection) {
            // First click - select start node
            startNodeForConnection = node.group;
            document.getElementById('add-connection').textContent = 'Select End Node';
          } else {
            // Second click - select end node and create connection
            if (startNodeForConnection !== node.group) {
              createConnection(startNodeForConnection, node.group);
            }
            
            // Reset
            isAddingConnection = false;
            startNodeForConnection = null;
            document.getElementById('add-connection').textContent = 'Add Connection';
            
            // Remove click events
            nodes.forEach(n => {
              n.group.off('click');
            });
          }
        }
      });
    });
  } else {
    // Cancel connection mode
    isAddingConnection = false;
    startNodeForConnection = null;
    document.getElementById('add-connection').textContent = 'Add Connection';
    
    // Remove click events
    nodes.forEach(node => {
      node.group.off('click');
    });
  }
});

// Add a node on double-click anywhere on the stage
stage.on('dblclick', (e) => {
  // Check if the click was on the stage (not on a node)
  if (e.target === stage) {
    const pos = stage.getPointerPosition();
    createNode(pos.x - NODE_WIDTH / 2, pos.y - NODE_HEIGHT / 2);
  }
});

// Remove transformers when clicking on the stage (outside of nodes)
stage.on('click', (e) => {
  // Only remove transformers if clicking directly on the stage
  if (e.target === stage) {
    // Hide all transformers
    nodes.forEach(node => {
      node.transformer.nodes([]);
    });
    layer.draw();
  }
});

// Initial draw
layer.draw();
