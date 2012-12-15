class ClassWithMethodsWithLocalVariablesInsideClosures{
    
    public m() {
        [].each { 
            int public_m_s_1
            def public_m_d_1 
            [].each {
                def public_m_d_2
            }
        }
    }
}