package pl.macieksob.rentCar.service;

import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.macieksob.rentCar.dto.UserDTO;
import pl.macieksob.rentCar.exception.UserDuplicateException;
import pl.macieksob.rentCar.exception.UserNotFoundException;
import pl.macieksob.rentCar.model.Car;
import pl.macieksob.rentCar.model.User;
import pl.macieksob.rentCar.repository.UserRepository;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MailService mailService;

    private UserDTO mapToDTO(User user){
        UserDTO map = modelMapper.map(user, UserDTO.class);
        return map;
    }

    private User mapToEntity(UserDTO userDTO){
        User user = modelMapper.map(userDTO, User.class);
        return user;
    }

    public User addUser(UserDTO user)  {
        if(userRepository.existsById(user.getId())){
            throw new UserDuplicateException("User already exists!");
        }
        User user1 = mapToEntity(user);
        user1.setPassword(passwordEncoder.encode(user.getPassword()));


        return userRepository.save(user1);
    }

    public void sendVerificationEmail(UserDTO user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject  = "Potwierdzenie rejestracji";
        String from = "RentCar";
        String message = "<p>Szanowny " + user.getName() + " " + user.getSurname() + ",</p>";
        message += "<p>Aby korzystać z konta musisz potwierdzić rejestrację. Zrobisz to klikając w poniższy link.</p>";
        String verifyURL = url + "/verify?code=" + user.getVerificationCode();
        message += "<h4><a href=\""+verifyURL+"\">WERYFIKACJA</a></h4>";

        message += "<p>Pozdrawiamy, <br> zespół RentCar!</p>";


        mailService.sendMail(from,"macieksob25@gmail.com",user.getEmail(), message,subject,true);
    }

    public void deleteUserById(Long id){
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException("User not found!");
        }
        userRepository.deleteById(id);
    }

    public User editUser(Long id, UserDTO editUser){
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new UserNotFoundException("User not found!");
        });

        user.setCity(editUser.getCity());
        user.setEmail(editUser.getEmail());
        user.setNumberOfFlat(editUser.getNumberOfFlat());
        user.setPhoneNumber(editUser.getPhoneNumber());
        user.setStreet(editUser.getStreet());
        user.setPostCode(editUser.getStreet());
        user.setCreatedTime(LocalDateTime.now());
        String make = RandomString.make(64);
        user.setVerificationCode(make);
        return userRepository.save(user);
    }

    public void deleteUser(UserDTO user){
        User user1 = mapToEntity(user);

        userRepository.delete(user1);
    }

//    public java.util.List<UserDTO> getByKeyword(String keyword){
//        return userRepository.findAllByKeyword(keyword, PageRequest.of(0,10)).stream().map(this::mapToDTO).collect(Collectors.toList());
//    }

    public boolean verify(String verificationCode){
        UserDTO user = userRepository.findByVerificationCode(verificationCode);

        if(user == null || user.getEnabled()){
            return false;
        }else {
            userRepository.setEnable(user.getId());

            return true;
        }

    }

    public void updateResetPasswordToken(String token, String email) throws UserNotFoundException{
        UserDTO user = userRepository.findByEmail(email);

        if(user != null){
            user.setResetPasswordToken(token);
            addUser(user);
        }else{
            throw new UserNotFoundException("User not found!");
        }
    }

    public UserDTO getUserByResetPasswordToken(String resetPasswordToken){
        return userRepository.findByResetPasswordToken(resetPasswordToken);
    }

    public void updatePassword(UserDTO user, String newPassword){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodePassword = bCryptPasswordEncoder.encode(newPassword);

        user.setPassword(encodePassword);
        user.setResetPasswordToken(null);

        addUser(user);
    }

}