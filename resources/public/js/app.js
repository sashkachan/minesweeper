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
	console.log(this.props.data);
	return (
	    <div>{children}</div>
	);
    }
});

var Cell = React.createClass({
    render: function () {
	return(
		<span className="cell">{this.props.data}</span>
	);
    }
});


var Field = React.createClass({
    getInitialState: function () {
	return {rows: [] };
    },

    loadGame: function (data) {
	var field = data.result.field.size;
	var rows = [];
	for (var i = 0; i < field[0]; i++) {
	    var cells = [];
	    for (var j = 0; j < field[1]; j++) {
		var cellId = i + "" + j;
	    	cells.push(<Cell key={cellId} data={cellId} />);
	    }
	    rows.push(<Row data={cells} />);
	}
	this.setState({rows: rows});
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
    makeMove: function () {

    },
    render: function () {
	console.log
	return(
	        <div>
	        <ControlBar newGameHandler={this.startGame}/>
		<div id="field">{this.state.rows}</div>
		</div>
	);
    }
});

React.render(
    <Field/>,
    document.getElementById("content")
);
