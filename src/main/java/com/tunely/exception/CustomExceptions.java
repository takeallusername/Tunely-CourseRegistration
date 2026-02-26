package com.tunely.exception;

public class CustomExceptions {

    public static class CourseNotFoundException extends RuntimeException {
        public CourseNotFoundException() {
            super("과목을 찾을 수 없습니다");
        }
    }

    public static class CourseFullException extends RuntimeException {
        public CourseFullException() {
            super("수강 정원이 가득 찼습니다");
        }
    }

    public static class AlreadyEnrolledException extends RuntimeException {
        public AlreadyEnrolledException() {
            super("이미 수강신청한 과목입니다");
        }
    }

    public static class EnrollmentNotFoundException extends RuntimeException {
        public EnrollmentNotFoundException() {
            super("수강신청 내역을 찾을 수 없습니다");
        }
    }

    public static class SessionNotFoundException extends RuntimeException {
        public SessionNotFoundException() {
            super("세션을 찾을 수 없습니다");
        }
    }

    public static class SessionAlreadyExistsException extends RuntimeException {
        public SessionAlreadyExistsException() {
            super("이미 진행 중인 세션이 있습니다");
        }
    }

    public static class InvalidUserIdException extends RuntimeException {
        public InvalidUserIdException() {
            super("유효하지 않은 사용자 ID입니다");
        }
    }
}
