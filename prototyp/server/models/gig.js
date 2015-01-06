var mongoose = require('mongoose');

var MyModel = new mongoose.Schema({
    band: { type: mongoose.Schema.Types.ObjectId, ref: 'User'},
    event: { type: mongoose.Schema.Types.ObjectId, ref: 'Event' },
    lineup: [{
        _id: false,
        user : { type: mongoose.Schema.Types.ObjectId, ref: 'User'},
        role : String
    }]
});


module.exports = mongoose.model('Gig', MyModel);