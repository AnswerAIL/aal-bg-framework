package com.answer.bdframework.entity;

/**
 * @author Answer.AI.L
 * @date 2019-05-14
 */
public enum ExecEnum {

    /** insert */
    INSERT {
        @Override
        public String getName() {
            return "insert";
        }
    },
    /** delete */
    DELETE {
        @Override
        public String getName() {
            return "delete";
        }
    },
    /** update */
    UPDATE {
        @Override
        public String getName() {
            return "update";
        }
    },
    /** select */
    SELECT {
        @Override
        public String getName() {
            return "select";
        }
    },
    /** select many */
    SELECT_MANY {
        @Override
        public String getName() {
            return "selectMany";
        }
    };


    public abstract String getName();
}
