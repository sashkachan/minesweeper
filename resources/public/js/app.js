var ControlBar = React.createClass({
    render: function () {
	return(
		<div>
		<NewGameForm newGameHandler={this.props.newGameHandler}/>
		</div>
	);
    }
});

var NewGameForm = React.createClass({
    render: function () {
	return (<div id="new_game">
		<form name="level_picker">
		<select name="level" id="game_level">
		<option value="easy">Easy</option>
		<option value="medium">Medium</option>
		<option value="hard">Hard</option>
		</select>
		<button onClick={this.props.newGameHandler}>Start</button>
		</form>
		</div>);
    }
});

var Row = React.createClass({
    getInitialState: function () {
	return {flaggedCells: []};
    },

    isFlaggedCell: function (cellId) {
	if (this.state.flaggedCells.indexOf(cellId) > -1) {
	    return true;
	}
	return false;
    },

    newGameHandler: function () {
	this.setState({flaggedCells: []});
    },
    
    handleContextClick: function (cellId, event) {
	var newFlaggedCells = this.state.flaggedCells;
	if (this.isFlaggedCell(cellId)) {
	    newFlaggedCells = newFlaggedCells.filter(function (elCellId) { return elCellId != cellId; });
	} else {
	    newFlaggedCells.push(cellId);
	}
	this.setState({flaggedCells: newFlaggedCells});
	event.preventDefault();
    },
    render: function () {
	var children = this.props.data.map(function (cell) {
	    var data = cell.data;
	    var flaggedCell = this.isFlaggedCell(data.cellId);
	    var contextHandler = this.handleContextClick.bind(this, data.cellId);
	    return <Cell key={data.cellId} data={data} clickHandler={cell.clickHandler} contextHandler={contextHandler} flagged={flaggedCell}/>
	}.bind(this));
	return (
	    <div>{children}</div>
	);
    }
});

var Cell = React.createClass({
    render: function () {
	var char;
	handler = this.props.clickHandler;
	if (this.props.data.flipped == false) {
	    if (this.props.flagged) {
		char = "b";
		handler = null;
	    } else {
		char = "_";
	    }
	} else if (this.props.data.isBomb == false) {
	    char = this.props.data.num.toString();
	    handler = null;
	} else {
	    char = "x";
	}
	return(
		<span className="cell" onClick={handler} onContextMenu={this.props.contextHandler}>{char}</span>
	);
    }
});


var Field = React.createClass({
    getInitialState: function () {
	return {rows: [], uid: null, newGame: true };
    },

    startGame: function (event) {
	var gameLevel = $("#game_level").val();
	var that = this;
	$.ajax("game-start/" + gameLevel, {
	    dataType: "json",
	    success: that.loadGame
	});
	event.preventDefault();
    },
    
    loadGame: function (data) {
	var field = data.result.field.size;
	var rows = [];
	for (var i = 0; i < field[0]; i++) {
	    var cells = [];
	    for (var j = 0; j < field[1]; j++) {
		var cellId = i + "." + j;
	    	cells.push({cellId: cellId, isBomb:null, num: null, flipped: false});
	    }
	    rows.push(cells);
	}

	this.setState({rows: rows, uid: data.result.uid});
    },
    
    makeMove: function (cellData, event) {
	var cell = cellData.cellId.split(".").map(
	    function (el) {
		return parseInt(el);
	    }
	);
	var makeMoveHandler = function (data) {
	    var game = data.result.game;
	    var rows = Array();
	    for (var index in game) {
		var cell = game[index];
		var cellId = cell.coord[0] + "." + cell.coord[1];
		var rowNum = cell.coord[0];
		var cellNum = cell["number"];
		if (rows[rowNum] == undefined) {
		    rows[rowNum] = Array();
		}
		rows[rowNum].push({
		    cellId: cellId,
		    isBomb: cell["is-bomb"],
		    flipped: (cellNum != null || (cell["is-bomb"] == true)),
		    num: cellNum
		});
	    }
	    this.setState({rows: rows, uid: this.state.uid});
	}.bind(this);
	
	$.ajax("move/" + this.state.uid, {
	    dataType: "json",
	    method: "POST",
	    data: {move: "[" + cell[0] + "," + cell[1] + "]"},
	    success: makeMoveHandler
	});
	event.preventDefault();
    },
    
    render: function () {
	var rows = this.state.rows.map(function(row) {
	    var cells = row.map(function (cell) {
		var clickHandler = this.makeMove.bind(this, cell);
		return {data: cell, clickHandler: clickHandler}
	    }, this);
	    return <Row data={cells} />;
	}, this);

	return(
	        <div>
	        <ControlBar newGameHandler={this.startGame}/>
		<div key={this.state.uid} id="field">{rows}</div>
		</div>
	);
    }
});

React.render(
    <Field/>,
    document.getElementById("content")
);
