var mongoose = require('mongoose');

var MyModel = new mongoose.Schema({
    _creator: { type: mongoose.Schema.Types.ObjectId, ref: 'User'},
    date : {type: Date, default: Date.now},
    file: mongoose.Schema.Types.ObjectId
});

module.export = mongoose.model('Audio', MyModel);