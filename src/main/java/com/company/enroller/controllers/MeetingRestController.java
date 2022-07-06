package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createMeeting(@RequestBody Meeting meeting) {
        meetingService.createMeeting(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") long meetingId){
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.deleteMeeting(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}/participants", method = RequestMethod.PUT)
    public ResponseEntity<?> addParticipant(@RequestParam long meetingId, @RequestParam String login) {
        Meeting foundMeeting = meetingService.findById (meetingId);
        Participant foundParticipant = participantService.findByLogin (login);
        if (foundMeeting == null || foundParticipant == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.addParticipant (foundMeeting, foundParticipant);
        return new ResponseEntity<Collection<Participant>> (foundMeeting.getParticipants (), HttpStatus.OK);
    }
    @RequestMapping(value = "{id}/participants/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteParticipant(@RequestParam long meetingId, @RequestParam String login) {
        Meeting foundMeeting = meetingService.findById (meetingId);
        Participant foundParticipant = participantService.findByLogin(login);
        if (foundMeeting == null || foundParticipant == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.deleteParticipant (foundMeeting, foundParticipant);
        return new ResponseEntity<Collection<Participant>> (foundMeeting.getParticipants (), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipants(@PathVariable("id") long meetingId) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Collection<Participant>> (meeting.getParticipants (), HttpStatus.OK);
    }
}
