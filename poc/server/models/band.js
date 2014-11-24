var mongoose = require('mongoose');

var MyModel = new mongoose.Schema({
    id: { type: Number, index: { unique : true } },
    bandName: String,
    country: String,
    city: String,
    genre: String,
    members: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User'}]
});

module.export = mongoose.model('Band', MyModel);