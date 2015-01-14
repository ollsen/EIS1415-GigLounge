var url = 'mongodb://localhost/prototyp';
var mongoose = require('mongoose');

// Gridfs setup
var Grid = require('gridfs-stream');
Grid.mongo = mongoose.mongo;
var conn = mongoose.createConnection(url);
module.exports = conn;