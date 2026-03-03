package com.backoffice.controller;

// import java.util.HashMap;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;

import itu.framework.annotations.MyController;
import itu.framework.annotations.MyURL;
import itu.framework.model.ModelView;

@MyController(value = "Test")
public class ControllerTest {

    @MyURL(value = "/", method = "GET")
    public ModelView home() {
        return new ModelView("index.jsp");
    }

    @MyURL(value = "/admin", method = "GET")
    public ModelView showAdmin() {
        return new ModelView("admin/dashboard.jsp");
    }

    // @MyURL(value = "/departement/{id}/{b}", method = "GET")
    // public ModelView getDepartementById(Integer id, String b) {
    // w mv = new ModelView("departement.jsp");
    // em("id", id);
    // em("b", b);
    // v;
    // }

    // @MyURL(value = "/form", method = "GET")
    // public ModelView showForm() {
    // ew ModelView("form.jsp");
    // }

    // @MyURL(value = "/form", method = "POST")
    // public ModelView submitForm(HashMap<String, Object> params) {
    // w mv = new ModelView("form-result.jsp");
    // em("name", params.get("name"));
    // em("age", params.get("age"));
    // v;
    // }

    // @MyURL(value = "/form/employe", method = "GET")
    // public ModelView showFormEmploye() {
    // ew ModelView("employeForm.jsp");
    // }

    // @MyURL(value = "/employe", method = "POST")
    // public ModelView submitFormEmploye(Employe e) {
    // w mv = new ModelView("employe.jsp");
    // em("employe", e);
    // v;
    // }

    // @MyURL(value = "/api/employe", method = "POST")
    // public JsonResponse submitFormEmployeJson(Employe e) {
    // null || e.getNom() == null || e.getNom().isEmpty()) {
    // nse.badRequest("Le nom de l'employé est requis");
    //
    // sonResponse.success(e, "Employé créé avec succès");
    // }

    // @MyURL(value = "/upload", method = "GET")
    // public ModelView showFormUpload() {
    // ew ModelView("upload.jsp");
    // }

    // @MyURL(value = "/upload", method = "POST")
    // public ModelView handleUpload(Map<String, byte[]> files) {
    // w mv = new ModelView("upload-result.jsp");
    // em("nbFiles", files != null ? files.size() : 0);
    // v;
    // }

}
