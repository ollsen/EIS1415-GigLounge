var mongoose = require('mongoose');

var MyModel = new mongoose.Schema({
    _creator: { type: mongoose.Schema.Types.ObjectId, ref: 'User'},
    date : {type: Date, default: Date.now},
    file: String
});

module.export = mongoose.model('Audio', MyModel);