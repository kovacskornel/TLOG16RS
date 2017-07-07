package com.kovacskornel.tlog16rs.resources;

import com.avaje.ebean.Ebean;
import com.kovacskornel.tlog16rs.core.InvalidJWTTokenException;
import com.kovacskornel.tlog16rs.core.MissingUserException;
import groovy.util.logging.Slf4j;

import java.time.LocalDate;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.avaje.ebean.EbeanServer;
import com.kovacskornel.tlog16rs.CreateDatabase;
import com.kovacskornel.tlog16rs.TLOG16RSConfiguration;
import com.kovacskornel.tlog16rs.core.NotNewMonthException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.NotAuthorizedException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;

/**
 * @author Kovacs Kornel
 */
@Path("/timelogger")
@Slf4j
public class TLOG16RSResource {

    TimeLogger tl = new TimeLogger("test");

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TLOG16RSResource.class);

    private final CreateDatabase database;
    private final EbeanServer ebeanServer;

    public TLOG16RSResource(TLOG16RSConfiguration config) {
        database = new CreateDatabase(config);
        ebeanServer = database.getEbeanServer();
    }

    private WorkMonth createMonthIfNotExists(TimeLogger tl, int year, int month) {
        int m;
        WorkMonth wm = null;
        if (!tl.getMonths().isEmpty()) {
            for (m = 0; m < tl.getMonths().size(); m++) {
                WorkMonth workMonth = tl.getMonths().get(m);
                if (workMonth.getDate().getYear() == year && workMonth.getDate().getMonthValue() == month) {
                    wm = workMonth;
                }
            }
        }
        if (wm == null) {
            wm = new WorkMonth(year, month);
            tl.addMonth(wm);
        }

        Ebean.save(tl);
        return wm;
    }

    private WorkDay createDayIfNotExists(TimeLogger tl, int year, int month, int day, long req) {
        WorkMonth mywm = createMonthIfNotExists(tl, year, month);
        WorkDay mywd = null;
        if (!mywm.getDays().isEmpty()) {
            int d;
            for (d = 0; d < mywm.getDays().size(); d++) {
                WorkDay wd = mywm.getDays().get(d);
                if (wd.getActualDay().getDayOfMonth() == day) {
                    mywd = wd;
                }
            }
        }
        if (mywd == null) {
            mywd = new WorkDay(LocalDate.of(year, month, day), req);

            mywm.addWorkDay(mywd);
            Ebean.save(tl);
        }
        return mywd;
    }

    private Task createTaskIfNotExists(TimeLogger tl, WorkDay mywd, String taskid, String stime, String comment) {
        Task myTask = null;
        if (!mywd.getTasks().isEmpty()) {
            int t;
            for (t = 0; t < mywd.getTasks().size(); t++) {
                if (mywd.getTasks().get(t).getTaskId().equals(taskid) && mywd.getTasks().get(t).getStartTime() == mywd.getTasks().get(t).stringToLocalTime(stime)) {
                    myTask = mywd.getTasks().get(t);
                    myTask.setTaskId(taskid);
                    myTask.setStartTime(stime);
                    if (comment != null) {
                        myTask.setComment(comment);
                    }
                }
            }
        }
        if (myTask == null) {
            myTask = new Task(taskid, stime, comment);
            mywd.addTask(myTask);

        }
        Ebean.save(tl);
        return myTask;
    }

    @Path("/workmonths")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWorkmonths(@HeaderParam("Authorization") String authorization) throws InvalidJwtException, Exception {
        try {
            TimeLogger user = getUserIfValidToken(authorization);
            return Response.ok(user.getMonths()).build();
        } catch (InvalidJWTTokenException e) {
            LOG.warn(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     * Adds a workmonth
     *
     * @param authorization
     * @param month int year, int month
     * @return workMonth
     * @throws java.lang.Exception
     */
    @POST
    @Path("/workmonths")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewMonth(@HeaderParam("Authorization") String authorization, WorkMonthRB month) throws Exception {
        try {
            TimeLogger user = getUserIfValidToken(authorization);

            WorkMonth workMonth;
            workMonth = new WorkMonth(month.getYear(), month.getMonth());
            user.addMonth(workMonth);
            Ebean.save(user);
            return Response.ok(workMonth).build();
        } catch (NotAuthorizedException | UnsupportedEncodingException | JoseException | InvalidJwtException ex) {
            LOG.error(ex.getMessage(), ex);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (NotNewMonthException ex) {
            LOG.error(ex.getMessage(), ex);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @Path("/workmonths/{year}/{month}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response monthData(@HeaderParam("Authorization") String authorization, @PathParam(value = "year") int year, @PathParam(value = "month") int month) {
        try {
            TimeLogger user = getUserIfValidToken(authorization);

            WorkMonth mywm;
            mywm = createMonthIfNotExists(user, year, month);
            return Response.ok(mywm.getDays()).build();
        } catch (InvalidJWTTokenException e) {
            LOG.warn(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }
    }

    @PUT
    @Path("/workmonths/deleteall")
    @Produces(MediaType.APPLICATION_JSON)
    public void delAll(@HeaderParam("Authorization") String authorization) throws InvalidJwtException, Exception {
        int i = 1;
        while (!Ebean.find(TimeLogger.class).findList().isEmpty()) {
            Ebean.delete(TimeLogger.class, i);
            i++;
        }
        TimeLogger user = getUserIfValidToken(authorization);
        Ebean.save(user);
    }

    @POST
    @Path("/workmonths/workdays")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewDay(@HeaderParam("Authorization") String authorization, WorkDayRB day) {
        try {
            TimeLogger user = getUserIfValidToken(authorization);
            createDayIfNotExists(user, day.getYear(), day.getMonth(), day.getDay(), day.getRequiredHours());
            return Response.status(200).build();
        } catch (InvalidJWTTokenException e) {
            LOG.warn(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }
    }

    @POST
    @Path("/workmonths/workdays/weekend")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewWeekendDay(@HeaderParam("Authorization") String authorization, WorkDayRB day) throws Exception {
        try {
            TimeLogger user = getUserIfValidToken(authorization);
            WorkMonth wm = createMonthIfNotExists(user, day.getYear(), day.getMonth());
            WorkDay wd = new WorkDay(LocalDate.of(day.getYear(), day.getMonth(), day.getDay()));
            wm.addWorkDay(wd);
            Ebean.save(user);
            return Response.status(200).build();
        } catch (InvalidJWTTokenException e) {
            LOG.warn(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (InvalidJwtException | OptimisticLockException e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }
    }

    @Path("/workmonths/{year}/{month}/{day}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response dayData(@HeaderParam("Authorization") String authorization, @PathParam(value = "year") int year, @PathParam(value = "month") int month, @PathParam(value = "day") int day) throws InvalidJwtException, Exception {
        try {
            TimeLogger user = getUserIfValidToken(authorization);
            WorkDay mywd = createDayIfNotExists(user, year, month, day, 450);
            return Response.ok(mywd.getTasks()).build();
        } catch (InvalidJWTTokenException e) {
            LOG.warn(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("/workmonths/workdays/tasks/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response startTask(@HeaderParam("Authorization") String authorization, StartTaskRB task) throws Exception {
        try {
            TimeLogger user = getUserIfValidToken(authorization);
            WorkDay mywd = createDayIfNotExists(user, task.getYear(), task.getMonth(), task.getDay(), 450);
            Task myTask = new Task(task.getTaskId(), task.getStartTime(), task.getComment());
            mywd.addTask(myTask);
            Ebean.save(user);
            return Response.ok(myTask).build();
        } catch (InvalidJWTTokenException e) {
            LOG.warn(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (InvalidJwtException | OptimisticLockException e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }

    }

    @PUT
    @Path("/workmonths/workdays/tasks/finish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response finishTask(@HeaderParam("Authorization") String authorization, FinishTaskRB task) throws Exception {
        try {
            TimeLogger user = getUserIfValidToken(authorization);
            WorkDay mywd = createDayIfNotExists(user, task.getYear(), task.getMonth(), task.getDay(), 450);
            Task myTask = createTaskIfNotExists(user, mywd, task.getTaskId(), task.getStartTime(), task.getComment());
            myTask.setEndTime(task.getEndTime());
            Ebean.save(user);
            return Response.ok(myTask).build();
        } catch (InvalidJWTTokenException e) {
            LOG.warn(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (InvalidJwtException | OptimisticLockException e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }
    }

    @PUT
    @Path("/workmonths/workdays/tasks/modify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyTask(@HeaderParam("Authorization") String authorization, ModifyTaskRB task) throws Exception {
        try {
            TimeLogger user = getUserIfValidToken(authorization);
            WorkDay mywd = createDayIfNotExists(user, task.getYear(), task.getMonth(), task.getDay(), 450);
            Task myTask = createTaskIfNotExists(user, mywd, task.getTaskId(), task.getStartTime(), task.getComment());
            myTask.setEndTime(task.getNewEndTime());
            myTask.setStartTime(task.getNewStartTime());
            myTask.setTaskId(task.getNewTaskId());
            Ebean.save(user);
            return Response.ok(myTask).build();
        } catch (InvalidJWTTokenException e) {
            LOG.warn(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (InvalidJwtException | OptimisticLockException e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }

    }

    private WorkMonth isMonthExists(TimeLogger tl, int year, int month) {
        int m;
        if (!tl.getMonths().isEmpty()) {
            for (m = 0; m < tl.getMonths().size(); m++) {
                WorkMonth wm = tl.getMonths().get(m);
                if (wm.getDate().getYear() == year && wm.getDate().getMonthValue() == month) {
                    return wm;
                }
            }
        }
        return null;
    }

    private WorkDay isDayExists(WorkMonth mywm, int day) {
        if (!mywm.getDays().isEmpty()) {
            int d;
            for (d = 0; d < mywm.getDays().size(); d++) {
                WorkDay wd = mywm.getDays().get(d);
                if (wd.getActualDay().getDayOfMonth() == day) {
                    return wd;
                }
            }
        }
        return null;
    }

    @PUT
    @Path("/workmonths/workdays/tasks/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTask(@HeaderParam("Authorization") String authorization, DeleteTaskRB task) throws Exception {
        try {
            try {
                TimeLogger user = getUserIfValidToken(authorization);
                Task delTask = null;
                WorkMonth mywm = isMonthExists(user, task.getYear(), task.getMonth());
                if (mywm == null) {
                    System.out.println("Month not found!");
                    return Response.ok(delTask).build();

                }
                WorkDay mywd = isDayExists(mywm, task.getDay());
                if (mywd == null) {
                    System.out.println("Day not found!");
                    return Response.ok(delTask).build();

                }
                if (!mywd.getTasks().isEmpty()) {
                    int t;
                    for (t = 0; t < mywd.getTasks().size(); t++) {

                        if (mywd.getTasks().get(t).getTaskId().equals(task.getTaskId()) && mywd.getTasks().get(t).getStartTime() == mywd.getTasks().get(t).stringToLocalTime(task.getStartTime())) {
                            System.out.println("Task found!");
                            delTask = mywd.getTasks().get(t);
                            System.out.println("Task " + delTask.getTaskId() + " deleted!");
                        }
                    }
                } else {
                    return Response.ok(delTask).build();
                }
                Ebean.delete(delTask);
                mywd.getTasks().remove(delTask);
                return Response.ok(delTask).build();
            } catch (InvalidJWTTokenException e) {
                LOG.warn(e.getMessage());
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (InvalidJwtException | OptimisticLockException e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }
    }

    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registering(UserRB user) throws NoSuchAlgorithmException, NotAuthorizedException, InvalidJwtException, UnsupportedEncodingException, JoseException {
        try {
            getUser(user.getName());
            LOG.error("try!");
            return Response.status(Response.Status.NOT_MODIFIED).build();
        } catch (MissingUserException e) {
            LOG.error("catch!");
            return registerUser(user);
        }
    }

    private Response registerUser(UserRB user) throws NoSuchAlgorithmException {
        TimeLogger user2 = new TimeLogger(user.getName());
        user2.setSalt(generateSalt());
        String hash = generatePasswordHash(user.getPassword(), user2.getSalt());
        user2.setPassword(hash);
        Ebean.save(user2);
        return Response.ok().build();
    }

    /**
     *
     * @param user
     * @return
     * @throws JoseException
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.UnsupportedEncodingException
     * @throws org.jose4j.jwt.consumer.InvalidJwtException
     */
    @Path("/authenticate")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticate(UserRB user) throws JoseException, NoSuchAlgorithmException, UnsupportedEncodingException, NotAuthorizedException, InvalidJwtException {
        try {
            TimeLogger user2 = getUser(user.getName());

            String hash = generatePasswordHash(user.getPassword(), user2.getSalt());
            if (hash.equals(user2.getPassword())) {
                return Response.ok(JwtService.createJWT(user2.getName())).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (MissingUserException e) {
            LOG.error(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @Path("/refresh")
    @POST
    public Response refresh(@HeaderParam("Authorization") String authorization) throws JoseException, InvalidJwtException, UnsupportedEncodingException, Exception {
        try {
            TimeLogger user = getUserIfValidToken(authorization);
            return Response.ok(JwtService.createJWT(user.getName())).build();
        } catch (InvalidJWTTokenException | SignatureException | ExpiredJwtException e) {
            LOG.warn(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @Path("/isExistUser")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response isExistUserName(String userName) throws NotAuthorizedException, UnsupportedEncodingException, JoseException, InvalidJwtException {
        try {
            getUser(userName);
            return Response.status(Response.Status.NOT_MODIFIED).build();
        } catch (MissingUserException e) {
            return Response.ok().build();
        }
    }

    public TimeLogger getUser(String token) throws NotAuthorizedException, UnsupportedEncodingException, JoseException, InvalidJwtException {
        if (token != null) {
            String jwt = token.split(" ")[1];
            String name = JwtService.getNameFromToken(jwt);
            TimeLogger user = Ebean.find(TimeLogger.class).where().eq("name", name).findUnique();
            return user;
        } else {
            throw new NotAuthorizedException("Not existing user");
        }
    }
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        String salt = new BigInteger(25, random).toString(32);
        return salt;
    }

    private String generatePasswordHash(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String digString = password + salt;
        byte[] hash = digest.digest(digString.getBytes(StandardCharsets.UTF_8));
        return String.format("%064x", new java.math.BigInteger(1, hash));
    }

    private TimeLogger getUserIfValidToken(String jwtToken) throws InvalidJwtException, Exception {
        String name = JwtService.getNameFromToken(jwtToken);
        return getUser(name);
    }

}