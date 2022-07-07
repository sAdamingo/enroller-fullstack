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
    @RequestMapping(value = "/{title}", method = RequestMethod.GET)
    public ResponseEntity<?> findMeetingByTitle(@PathVariable("title") String meetingTitle){
        Collection<Meeting> meetings = meetingService.findByTitle(meetingTitle);
        if (meetings == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Meeting meeting = meetings.stream().findFirst().get();
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createMeeting(@RequestBody Meeting meeting) {
        long meetingId = meeting.getId();
        Meeting meetingFoundById = meetingService.findById(meetingId);
        if (meetingFoundById != null) {
            return new ResponseEntity<>("Unable to create. A meeting with id " + meetingId + " already exist.", HttpStatus.CONFLICT);
        }
        String title = meeting.getTitle();
        Collection<Meeting> meetingsFoundByTitle = meetingService.findByTitle(title);
        if (meetingsFoundByTitle.size() != 0) {
            return new ResponseEntity<>("Unable to create. A meeting with title " + title + " already exist.", HttpStatus.CONFLICT);
        }
        meetingService.createMeeting(meeting);
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable String id){
        Meeting meeting = meetingService.findById(Long.parseLong(id));
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.deleteMeeting(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}/participants", method = RequestMethod.PUT)
    public ResponseEntity<?> addParticipant(@PathVariable String id, @RequestBody String login) {
        Meeting foundMeeting = meetingService.findById (Long.parseLong(id));
        Participant foundParticipant = participantService.findByLogin (login);
        if (foundMeeting == null || foundParticipant == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.addParticipant (foundMeeting, foundParticipant);
        return new ResponseEntity<Collection<Participant>> (foundMeeting.getParticipants (), HttpStatus.OK);
    }
    @RequestMapping(value = "{id}/participants/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteParticipant(@PathVariable String id, @PathVariable String login) {
        Meeting foundMeeting = meetingService.findById (Long.parseLong(id));
        Participant foundParticipant = participantService.findByLogin(login);
        if (foundMeeting == null || foundParticipant == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.deleteParticipant (foundMeeting, foundParticipant);
        return new ResponseEntity<Collection<Participant>> (foundMeeting.getParticipants (), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipants(@PathVariable String id) {
        Meeting meeting = meetingService.findById(Long.parseLong(id));
        if (meeting == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Collection<Participant>> (meeting.getParticipants (), HttpStatus.OK);
    }
}
