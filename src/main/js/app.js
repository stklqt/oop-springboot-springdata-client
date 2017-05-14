'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {events: []};
    }

    componentDidMount() {
        client({method: 'GET', path: '/events?projection=inlineSpeaker'}).done(response => {
            this.setState({events: response.entity._embedded.events});
    })
    }

    render() {
        return (
            <EventList events={this.state.events}/>
        )
    }
}

class EventList extends React.Component {
    render() {
        var events = this.props.events.map(event =>
            <Event key={event._links.self.href} event={event}/>
        );
        return (
            <table className="table table-striped">
                <tbody>
                <tr>
                    <th>Title</th>
                    <th>Description</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Room</th>
                    <th>Track</th>
                    <th>Speakers</th>
                </tr>
                {events}
                </tbody>
            </table>
        )
    }
}

class Event extends React.Component {
    render() {
        return (
            <tr>
                <td>{this.props.event.title}</td>
                <td>{this.props.event.description}</td>
                <td>{this.props.event.startTime}</td>
                <td>{this.props.event.endTime}</td>
                <td>{this.props.event.room}</td>
                <td>{this.props.event.track}</td>
                <SpeakerList>{this.props.event.speakers}</SpeakerList>
            </tr>
        )
    }
}

class SpeakerList extends React.Component {
    render() {
        var speakers = this.props.children.map(speaker =>
            <Speaker speaker={speaker}/>
        );
        return (
            <td>{speakers}</td>
        )
    }
}

class Speaker extends React.Component {
    render() {
        return (
            <div>{this.props.speaker.firstName} {this.props.speaker.lastName}</div>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
);