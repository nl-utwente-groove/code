<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n1"/>
        <node id="n3"/>
        <node id="n0"/>
        <node id="n2"/>
        <node id="n4"/>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>b</string>
            </attr>
        </edge>
        <edge from="n2" to="n1">
            <attr name="label">
                <string>a_to_a</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>b</string>
            </attr>
        </edge>
        <edge from="n0" to="n4">
            <attr name="label">
                <string>c_to_b</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge from="n2" to="n3">
            <attr name="label">
                <string>a_to_b</string>
            </attr>
        </edge>
        <edge from="n3" to="n4">
            <attr name="label">
                <string>b_to_b</string>
            </attr>
        </edge>
        <edge from="n1" to="n0">
            <attr name="label">
                <string>a_to_c</string>
            </attr>
        </edge>
    </graph>
</gxl>
