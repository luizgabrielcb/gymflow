ALTER TABLE public.physical_assessment RENAME TO physical_assessments;

ALTER TABLE public.physical_assessments
    DROP CONSTRAINT fk_user,
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public.users(id);