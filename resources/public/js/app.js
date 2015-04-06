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
    render: function () {
	var children = this.props.data;
	return (
	    <div>{children}</div>
	);
    }
});

var Cell = React.createClass({
    getInitialState: function () {
	return {flagged:false}
    },

    handleContextClick: function (event) {
	this.setState({flagged: !this.state.flagged});
	event.preventDefault();
    },
    
    render: function () {
	var char;
	handler = this.props.clickHandler;
	if (this.props.data.flipped == false) {
	    if (this.state.flagged) {
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
		<span className="cell" onClick={handler} onContextMenu={this.handleContextClick}>{char}</span>
	);
    }
});


var Field = React.createClass({
    getInitialState: function () {
	return {rows: [], uid: null };
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
	var that = this;
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
	    that.setState({rows: rows, uid: that.state.uid});
	};
	
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
		return <Cell key={cell.cellId} data={cell} clickHandler={clickHandler}/>
	    }, this);
	    return <Row data={cells} />;
	}, this);

	return(
	        <div>
	        <ControlBar newGameHandler={this.startGame}/>
		<div id="field">{rows}</div>
		</div>
	);
    }
});

React.render(
    <Field/>,
    document.getElementById("content")
);
