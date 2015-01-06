var mongoose = require('mongoose');

var MyModel = new mongoose.Schema({
    gig: { type: mongoose.Schema.Types.ObjectId, ref: 'Gig' },
    creation_date: {type: Date, default: Date.now()},
    musician: { type: mongoose.Schema.Types.ObjectId, ref: 'User'},
    ok: { type: Boolean, default: false},
    comments: [{
        author: { type: mongoose.Schema.Types.ObjectId, ref: 'User'},
        creation_date: {type: Date, default: Date.now()},
        comment: String
    }],
    media: [{
        author: { type: mongoose.Schema.Types.ObjectId, ref: 'User'},
        creation_date: {type: Date, default: Date.now()},
        media: mongoose.Schema.Types.ObjectId
    }]
});


module.exports = mongoose.model('Casting', MyModel);