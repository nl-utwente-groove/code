<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n1"/>
        <node id="n3"/>
        <node id="n6"/>
        <node id="n2"/>
        <node id="n4"/>
        <node id="n5"/>
        <node id="n7"/>
        <node id="n8"/>
        <node id="n0"/>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>5</string>
            </attr>
        </edge>
        <edge from="n2" to="n1">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>1</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>current</string>
            </attr>
        </edge>
        <edge from="n8" to="n5">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n0" to="n2">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n8" to="n8">
            <attr name="label">
                <string>3</string>
            </attr>
        </edge>
        <edge from="n5" to="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>2</string>
            </attr>
        </edge>
        <edge from="n1" to="n8">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>4</string>
            </attr>
        </edge>
        <edge from="n4" to="n7">
            <attr name="label">
                <string>v</string>
            </attr>
        </edge>
        <edge from="n4" to="n6">
            <attr name="label">
                <string>h</string>
            </attr>
        </edge>
        <edge from="n6" to="n7">
            <attr name="label">
                <string>v</string>
            </attr>
        </edge>
    </graph>
</gxl>
