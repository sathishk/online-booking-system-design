import React from 'react';
import { yupResolver } from '@hookform/resolvers/yup';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import * as Yup from 'yup';
import authService from '../services/auth.service';
import { useState } from 'react';

const RegisterComponent = () => {

  const navigate = useNavigate();
  const [isSubmitted, setIsSubmitted] = useState();
  const { authtoken } = useParams();

  const schema = Yup.object().shape({
    firstName: Yup.string().required(),
    lastName: Yup.string().required(),
    dob: Yup.date()
    .max(new Date(Date.now() - 567648000000), "You must be at least 18 years")
    .required("Required")
  });

  const { register, handleSubmit, formState: { errors, isDirty, isValid } } = useForm({
    mode: 'all',
    resolver: yupResolver(schema)
  });

  const handleValidSubmit = async (data) => {
    setIsSubmitted(true)
    try {
    console.log(authtoken)
      const config = {
        headers:{
        "content-type": "application/json",
        "authorization": "Bearer " + authtoken,
      }}
      const result = await authService.register(data, config);
      if (result.data) {
        console.log(result.data)
        // navigate('/profile');
      }
    } catch (error) {
      toast.error(error.data.message);
    }
    setIsSubmitted(false)
  }

  return (
    <div className="row">
      <div className="col-6 offset-3">
        <form onSubmit={handleSubmit(handleValidSubmit)}>
          <div className="mb-3">
            <label htmlFor="inputFirstName" className="form-label">FirstName</label>
            <input type="name" className="form-control" id="inputFirstName" {...register('firstName')} />
            <div className="form-text text-danger">
              {errors.firstName && <p>{errors.firstName.message}</p>}
            </div>
          </div>
          <div className="mb-3">
            <label htmlFor="inputLastName" className="form-label">LastName</label>
            <input type="name" className="form-control" id="inputLastName" {...register('lastName')} />
            <div className="form-text text-danger">
              {errors.lastName && <p>{errors.lastName.message}</p>}
            </div>
          </div>
          <div className="mb-3">
            <label htmlFor="inputDate" className="form-label">Date-of-Birth</label>
            <input type="date" className="form-control" id="inputDate" {...register('dob')} />
            <div className="form-text text-danger">
              {errors.dob && <p>{errors.dob.message}</p>}
            </div>
          </div>
          <button type="submit" className="btn btn-primary" disabled={isSubmitted || !isDirty || !isValid}>Submit</button>
        </form>
      </div>
    </div>
  )
}

export default RegisterComponent